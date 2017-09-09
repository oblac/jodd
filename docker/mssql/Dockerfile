FROM microsoft/mssql-server-linux

ENV ACCEPT_EULA=Y
ENV SA_PASSWORD=root!R00t!

COPY create-db.sql .
COPY create-db.sh .

RUN chmod +x ./create-db.sh

CMD /bin/bash ./create-db.sh