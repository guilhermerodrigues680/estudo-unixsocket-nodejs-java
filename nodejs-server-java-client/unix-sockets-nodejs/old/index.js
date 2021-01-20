const net = require('net');
const unixSocketServer = net.createServer();

unixSocketServer.listen('/tmp/unixSocket', () => {
  console.log('now listening');
});

unixSocketServer.on('connection', (s) => {
  console.log('got connection!');
  s.write('hello world');
  s.end();
});

unixSocketServer.on('data', (s) => {
    console.log(s)
})