# Commands to invoke SendMail for postal customers
cat smtp/Alan.send.txt | sendmail -i -t
cat smtp/Linda.send.txt | sendmail -i -t
cat smtp/Vicki.send.txt | sendmail -i -t