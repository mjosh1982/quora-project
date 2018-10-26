# quora-project

quora social web-app project for submission. This project is used for providing RST API using spring boot for various commmon functionalities for social web-app we plan to develop which is quora.

We will develop the application backend in Spring boot. 

Next section, we will define the pre-requisites for this application.

##Pre-Requisites

1. You would need to have maven setup on your machine.
2. We need post gresql driver which needs to be added to the dependency of the project.
3. Intellij idea IDE for development.

##Steps for installing the project.

1. Import the project in Intellij idea using option "Checkout from Version Control"

2. Select git as the option

3. Use the URL https://github.com/mjosh1982/quora-project.git to clone the repository on your local machine

4. Once the project gets imported in you local workspace, you need to add the jar file postgresql in your classpath.

5. right click on the postgresql jar file and select "Add to library"

6. Then goto Application.yaml and for the Postgresql driver select suggesstion in red and select add to classpath

7. This will add the file to classpath.

8. Goto file QuoraApiApplication, right click and run it as a Java application.

