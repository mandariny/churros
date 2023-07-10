FROM mongo:4.2

COPY ./mongod.conf /etc/mongod.conf

RUN /usr/bin/mongod --fork --logpath /var/log/mongodb.log --config /etc/mongod.conf && \
    sleep 10 && \
    mongo --eval 'rs.initiate({ _id: "churros", version: 1, members: [{ _id: 0, host: "localhost:27017" }]})' && \
    sleep 10 && \
    mongo admin --eval 'db.getSiblingDB("admin").createUser({user: "admin", pwd: "admin123", roles: [{role: "root", db: "admin"}]})' && \
    sleep 10 && \
    mongo admin -u admin -p admin123 --eval 'db.getSiblingDB("newsdb").createUser({user: "churros", pwd: "A503", roles: [{role: "readWrite", db: "newsdb"}]})' && \
    sleep 10 && \
    mongo admin -u admin -p admin123 --eval 'db.getSiblingDB("newsdb").grantRolesToUser("churros", [{ role: "read", db: "local", collection: "oplog.rs" }])' && \
    /usr/bin/mongod --shutdown
EXPOSE 27017

CMD ["mongod", "--bind_ip_all", "--config", "/etc/mongod.conf"]
