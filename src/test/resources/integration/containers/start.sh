docker start idmu-mysql
docker start idmu-mongo
docker start idmu-db2
docker start idmu-rest
docker start idmu-cloudant
docker exec idmu-cloudant bash /app/initialize.sh
