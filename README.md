## App Description

This application's purpose is to find the maximum amount of tasks that can be completed in a given time interval.

The app reads the input from the ```releases.txt``` file from the root directory which is required to have the following format:
- Each line contains two digits separated by a space
- The first digit represents the day the task is available for testing
- The second digit represents the estimated duration for testing the task

Executing the app will generate the output file to ```artifacts/solution.txt```</br>
The output file has the following format:
- The first line contains a digit which counts the successfully executed tasks
- The following lines contain the start and end day of executing a task separated by a space

## Run in IntelliJ

Select the ```MainKt``` configuration from the run configurations, which will execute the app in your IDE.

## Run in local environment

First make sure that the app is built by running the ```make build``` command.</br>
It will build the app including its dependencies saving them to the output folder.
Then run ``make run`` to execute the application

## Run in docker environment

If your local environment doesn't have IntelliJ or the necessary libraries installed to run the project, consider running it inside Docker.</br>
First make sure that the image is built by running the ```make docker-build``` command.</br>
To launch the container, run ```make docker-run```. 
When starting the container, it will copy your local ```releases.txt``` file in the container, so any modification will be reflected.</br>
After the app is executed in the container, it will copy the ```solution.txt``` artifact to the local ```artifacts``` folder.