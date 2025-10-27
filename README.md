# To Do App

---

## Table of Contents

1. [Live Link](#live-link)
2. [Setup Locally - Getting Started](#getting-started)
3. [Option 1](#option-1-running-the-application)
4. [Option 2 - With Docker](#option-2-docker-run)

---

## Live link

```bash
   https://main.d3ax1nadzcu8et.amplifyapp.com/signin
```

## Getting Started

1. Clone the repository:
   ```bash
   https://github.com/vishnaka-dasanayaka/todo-frontend.git
   ```

---

## Option 1 Running the Application

1. Open the project with a IDE (ex:IntelliJ IDEA)
2. Navigate to src -> main -> java -> resources -> appliacation.properties
3. Comment line 4 (line below the LINK TO RDS DB comment)
4. Uncomment line 7 (line below the LINK FOR LOCAL SETUP comment)
5. Change the port number for your MySQL localhost 
6. Change the username and the password for your MySQL localhost 
7. Install maven dependencies
8. Start the App 

---

## Option 2 Docker Run

```bash
   cd todo-frontend
   docker build -t todo-image .
   
```
