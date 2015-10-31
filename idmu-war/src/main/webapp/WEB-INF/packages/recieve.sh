if [ "$#" -ne 3 ]; then
    echo "Usage is receive.sh from toPort collections"
    echo "Example: ./promote.sh devhost:8080 9090 myCollection"
    echo "Run from the Production Server on port 9090, from packages directory"
    exit 1;
fi

curl $1/idmu/templatePackage/$3 > $3.json
curl -X POST localhost:$2/idmu/templatePackage/$3.json > $3.validate.json
if $(diff $3.json $3.validate.json); then
	echo "Success"
	rm $3.validate.json
else
	echo "ERROR - Packages failed to load properly!!!"
fi


