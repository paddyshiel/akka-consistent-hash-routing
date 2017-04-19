'use strict';

const _ = require('lodash');
const amqp = require('amqplib');

const rabbitConnString = "amqp://localhost:5672";
const exchangeName = "inbound.exchange";
const exchangeType = "topic";
const routingKey = "event.";
const publishTimeInMillis = 100;

const numberOfEvents = 99999;
const numberOfDifferentEventIds = 10;

console.info(`[Rabbit-Publisher] Publishing messages ${exchangeName} at ${rabbitConnString} | ${exchangeName}`);

const createChannel = (connection) => (connection.createChannel().then((channel) => ({connection, channel})) );

const assertExchangeExists = ({connection, channel}) => (
    channel.assertExchange(exchangeName, exchangeType).then((exchange) => ({connection, channel}))
);

const publishMessages = ({connection, channel}) => {
    const publishMessage = (msgNum) => {
        const eventId = msgNum % numberOfDifferentEventIds;
        const messageBuffer = new Buffer(`{ "id": "${eventId}", "name": "Some Event ${eventId} ${msgNum}" }`);
        channel.publish(exchangeName, routingKey + msgNum, messageBuffer);
        console.info(`[Rabbit-Publisher] Published: ${messageBuffer}`);
    };

    _.forEach(_.range(numberOfEvents), i => publishMessage(i));
    return {connection, channel};
};

const close = ({connection, channel}) => {
    console.log(`[Rabbit-Publisher] Waiting ${publishTimeInMillis} ms for publishing to complete...`);
    setTimeout(() => {
        channel.close();
        connection.close();
        console.info(`[Rabbit-Publisher] Connection and Channel closed.`);
    }, publishTimeInMillis);
};

const handleError = (err) => console.error(`[Rabbit-Publisher] Error: ${err.message}`);

amqp
    .connect(rabbitConnString)
    .then(createChannel)
    .then(assertExchangeExists)
    .then(publishMessages)
    .then(close)
    .catch(handleError);