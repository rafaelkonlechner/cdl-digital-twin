FROM rabbitmq:3.6.6-management-alpine

RUN rabbitmq-plugins enable --offline rabbitmq_mqtt

# Fix nodename
RUN echo 'NODENAME=rabbit@localhost' > /etc/rabbitmq/rabbitmq-env.conf

EXPOSE 1883
EXPOSE 5672
EXPOSE 15672

