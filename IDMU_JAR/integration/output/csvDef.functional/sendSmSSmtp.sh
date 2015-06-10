# Commands to invoke SendMail and SMS Default Command Template
curl http://textbelt.com/text -d number{phone} -d message=Time to Renew!Pleace visit www.mypage.net to make sure we have your eMail address
cat smtp/Alan.send | sendmail -i -tDefault Command Template
curl http://textbelt.com/text -d number{phone} -d message=Time to Renew!
cat smtp/Linda.send | sendmail -i -tDefault Command Template
curl http://textbelt.com/text -d number{phone} -d message=Time to Renew!
cat smtp/Vicki.send | sendmail -i -tDefault Command Template