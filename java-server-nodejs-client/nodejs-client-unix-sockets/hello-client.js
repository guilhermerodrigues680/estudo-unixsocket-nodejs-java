const ipc = require('node-ipc');

/***************************************\
 *
 * You should start both hello and world
 * then you will see them communicating.
 *
 * *************************************/

ipc.config.id = 'hello';
ipc.config.retry = 1000;

ipc.connectTo('world', function () {
  console.log('con', ipc.of.world.destroy);
});

ipc.of.world.on('connect', function () {
    ipc.log('## connected to world ##', ipc.config.delay);


    // ipc.of.world.emit('app.message-ola',
    // {
    //   id: ipc.config.id,
    //   message: 'hello'
    // });
});

ipc.of.world.on('disconnect', function () {
  ipc.log('disconnected from world');
});

ipc.of.world.on('app.message', function (data) {
  ipc.log('got a message from world : ', data);
});

// Handle input from stdin.
process.stdin.on("data", function (data) {
  let inputbuffer = "";
  inputbuffer += data;
  
  if (inputbuffer.indexOf("\n") !== -1) {
    const line = inputbuffer.substring(0, inputbuffer.indexOf("\n"));
    inputbuffer = inputbuffer.substring(inputbuffer.indexOf("\n") + 1);
    
    // Let the client escape
    if (line === 'exit') {
      return cleanup();
    }
    
    if (line === 'quit') {
      return cleanup();
    }
    
    ipc.of.world.emit('app.message', { id: ipc.config.id, message: line });
  }
});
