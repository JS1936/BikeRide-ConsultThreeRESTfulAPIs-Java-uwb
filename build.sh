# This is build.sh 
# if permission denied, please do: "chmod +x build.sh" in console
# to run in console: ./build.sh
# to see individual .json data files for a specific city, test only one city at a time
#	note: to see only data for one city, clear the output files first

# compile. Relies on gson, JSON, apache's FileUtils
javac -cp .:gson-2.9.0.jar:java-json.jar:org.apache.commons.io.jar *.java


# single word city with capital first letter (follows format)
java -cp .:gson-2.9.0.jar:java-json.jar:org.apache.commons.io.jar MyCity Chicago 

# lowercase single word (does not follow format, but gets adjusted to fit format)
java -cp .:gson-2.9.0.jar:java-json.jar:org.apache.commons.io.jar MyCity chicago

# two word city
java -cp .:gson-2.9.0.jar:java-json.jar:org.apache.commons.io.jar MyCity "New Orleans"


# degraded functionality check 1
# expect: 
#	weather --> connection failure (404) 3 retries
#	bikes	--> connects but can't find the city,
#	aqi	--> connection failure (400) 3 retries,
java -cp .:gson-2.9.0.jar:java-json.jar:org.apache.commons.io.jar MyCity does-not-exist

# degraded functionality check 2
# expect:
#java -cp .:gson-2.9.0.jar:java-json.jar:org.apache.commons.io.jar MyCity Arlington
