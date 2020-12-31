// var os = require('os');
// var nodeStatic = require('node-static');
// var fileServer = new(nodeStatic.Server)();
// var socketIO = require('socket.io');
//
// const https = require('https');
// const fs = require('fs');
//
// const options = {
//     key: fs.readFileSync('./private.pem'),
//     cert: fs.readFileSync('./public.pem')
// };
// console.log("kkkkcreateServer")
// let app = https.createServer(options,
//     (req,res)=>{
//     console.log(res+" ################### ");
//     fileServer.serve(req, res);
// }).listen(3001);
// var io = socketIO.listen(app);

const os = require('os');
const app = require('express')();
const https = require('https');
const fs = require('fs');
const PORT = 3000;   // HTTPS 는 443 포트를 사용합니다

//var privateKey = fs.readFileSync('../ssl/private.key').toString();
//var certificate = fs.readFileSync('../ssl/private.crt').toString();
// var ca = fs.readFileSync('../ssl/rootCA.pem').toString();

const options = {
    key: fs.readFileSync('./private.pem'),
    cert: fs.readFileSync('./public.pem')
};

process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0"
// https 서버를 만들고 실행시킵니다
var server = https.createServer(options, app).listen(PORT);


var io = require('socket.io')(server); //setting cors

app.all('/*', function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With");
    next();
});

console.log('Started chating server...');


io.sockets.on('connection', function(socket) {

    // convenience function to log server messages on the client
    function log() {
        var array = ['Message from server:'];
        array.push.apply(array, arguments);
        socket.emit('log', array);
    }

    socket.on('message', function(message) {
        log('Client said: ', message);

        if (message==="bye" && socket.rooms['foo']) {
            io.of('/').in('foo').clients((error, socketIds) => {
                if (error) throw error;

                socketIds.forEach(socketId => {
                    //	if (socket.id===socketId) console.log('-------------------************');
//			else socket.broadcast.emit('message', message);
                    io.sockets.sockets[socketId].leave('foo');
                });

            });
        } //else {
        // for a real app, would be room-only (not broadcast)
        socket.broadcast.emit('message', message);

    });

    // socket.on('roommk',room){
    //     socket.emit('roommk', room);
    // });

    socket.on('create or join', function(room) {
        log('Received request to create or join room ' + room);
        var clientsInRoom = io.sockets.adapter.rooms[room];
        var numClients = clientsInRoom ? Object.keys(clientsInRoom.sockets).length : 0;
        log('Room ' + room + ' now has ' + numClients + ' client(s)');

        if (numClients === 0) {
            socket.join(room);
            log('Client ID ' + socket.id + ' created room ' + room);
            socket.emit('created', room, socket.id);
            console.log('created');
        } else if (numClients === 1) {
            log('Client ID ' + socket.id + ' joined room ' + room);
            io.sockets.in(room).emit('join', room);
            socket.join(room);
            socket.emit('joined', room, socket.id);
            io.sockets.in(room).emit('ready');
            console.log('joined');
        } else { // max two clients
            socket.emit('full', room);
        }
    });

    socket.on('ipaddr', function() {
        var ifaces = os.networkInterfaces();
        for (var dev in ifaces) {
            ifaces[dev].forEach(function(details) {
                if (details.family === 'IPv4' && details.address !== '127.0.0.1') {
                    socket.emit('ipaddr', details.address);
                }
            });
        }
    });

    socket.on('bye', function(){
        console.log('received bye');
    });

});
