import MySQLdb
import csv
db = MySQLdb.connect(host='frankencluster.com', user='g02dbf15admin', passwd='tw1pvTQq2E(A', db='g02dbf15')

with open('nasdaqlisted.txt', 'r') as csvfile:
	mf_reader = csv.reader(csvfile, delimiter='|')
	next(mf_reader, None)
	for row in mf_reader:
		symbol = row[0]
		name = row[1]
		stmt = "INSERT INTO `index`(symbol, name) VALUES('{}', '{}');".format(symbol, name.replace("'", "''"))
		print stmt
		# db.query(stmt)

