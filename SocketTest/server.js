var app = require('express')();

const PORT = 3001;   // HTTPS 는 443 포트를 사용합니다
const fs = require('fs');
const options = {
    key: fs.readFileSync('./private.pem'),
    cert: fs.readFileSync('./public.pem')
};

var server = require('https').createServer(options, app);
var io = require('socket.io')(server); //setting cors
app.all('/*', function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With");
    next();
});
// app.get('/', function (req, res) {
//     res.sendFile('Hellow Chating App Server');
// });
//connection event handler
io.on('connection', function (socket) {
    console.log('Connect from Client: ' + socket.id)
    socket.on('chat', function (data) {
        console.log('message from Client: ' + data.message)
        var rtnMessage = {message: data.message
            ,socketId : data.socketId};

        // 클라이언트에게 메시지를 전송한다
        socket.broadcast.emit('chat', rtnMessage);
    });
})
server.listen(PORT, function () {
    console.log('socket io server listening on port 3001')
})
