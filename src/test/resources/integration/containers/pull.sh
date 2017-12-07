./stop.sh
./remove.sh
docker run -p 3306:3306 --name idmu-mysql -d flatballflyer/idmu-test-mysql:latest
docker run -p 27017:27017 --name idmu-mongo -d flatballflyer/idmu-test-mongo:latest
docker run -p 50000:50000  --name idmu-db2 -v $(pwd):/share -d flatballflyer/idmu-test-db2:latest /data/superInit.sh
docker run -p 8383:80 --name idmu-rest -d flatballflyer/idmu-test-rest:latest
docker run -p 81:80 --name idmu-cloudant -d flatballflyer/idmu-test-cloudant:latest;docker exec idmu-cloudant /app/initialize.sh
