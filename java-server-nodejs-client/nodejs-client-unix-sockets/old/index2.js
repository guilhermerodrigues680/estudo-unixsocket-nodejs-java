const net = require('net');
const client = net.createConnection("/tmp/unixSocket");

client.on("connect", function(s) {
    console.log('connect')
    client.write('Oi!')
});

client.on("data", function(data) {
    console.log('data', data.toString())
});

