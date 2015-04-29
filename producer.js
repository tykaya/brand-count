var async = require('async');
var kafka = require('kafka-node-master'),
Producer = kafka.Producer,
client = new kafka.Client('localhost:2181'),
producer = new Producer(client);
var brand = ['Arcelik','Vestel','Ariston','Samsung','Altus','Regal','Electrolux','Ugur','Sharp','Philips'];
producer.on('ready', function () {
async.parallel([generate(0,250),generate(250,500),generate(500,750),generate(750,1000)],
        function(err, result){});
function generate(p,r){
        while(p<r){
                a = Math.floor(Math.random() * (11 - 1) + 1);
                producer.send([ { topic: 'test', messages:brand[a-1], partition: 0}], function(err, data){ });
                p++;
        }
}
});
producer.on('error', function (err) {})
