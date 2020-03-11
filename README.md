# Parcel tracker - simple containerized database application

Project started as an exercise for Helsinki University Basic of Database course which I decided then to containerize with Docker. It is developed using JDK 11 and Sqlite3. Program runs in a container based on a Debian image.

The aim of this project was to learn:
- managing database connections in Java
- creating relational databases and schemas
- usage of OO structures in data handling
- indexing data and its trade-offs in database performance
- ACID principals in transactions
- effective SQL querying by using indexes and joins
- development inside Docker containers

Inside the Docker container, there is a system that launches when the container runs. It creates a database parcels.db and starts asking the user which actions the user wants to take. User may
- add data such as orderers, event, parcels or places to the database
- query events related to a parcel
- query all parcels and amount of event of a certain orderer
- query number of events at a place by given date
- run performance test with or without additional indexes
- quit the program

To run this project install yourself a fit [Docker tool](https://www.docker.com/) and clone this project.

Inside the repository do:
```
docker build -t parcel_tracker .
```

When the image build is ready run the image with:
```
docker run -it --rm parcel_tracker
```

And follow the instructions!
