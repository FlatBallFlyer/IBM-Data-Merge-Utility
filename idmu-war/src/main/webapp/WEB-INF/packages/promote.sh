if [ "$#" -ne 3 ]; then
    echo "Usage is promote.sh from to collections"
    echo "Example, from the Development server: ./promote.sh localhost:8080 prodhost.net myCollection"
    echo "NOTE: This will not work if the target server is operating in "Secure Mode"
    exit 1;
fi

curl $1/idmu/templatePackage/$3 > $3.json
curl --upload $3.json $2/idmu/templatePackage > $3.validate.json
if $(diff $3.json $3.validate.json); then
	echo "Success"
	rm $3.validate.json
else
	echo "ERROR - Packages failed to load properly!!!"
fi


