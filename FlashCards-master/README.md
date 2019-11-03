# FlashCards

#### Building and Running the Project

There are 2 easy ways to build and run the project.

1. Use the NetBeans IDE, which can be downloaded from https://netbeans.org/downloads/8.2/. From within NetBeans, click "Open Project" and choose the FlashCards folder downloaded from GitHub. The folder already contains what you need for a NetBeans project. You can make changes to the code (if desired) and click the "Run Project" button, and the project will be built automatically before running.
2. If you don't want to use the NetBeans IDE, then you can edit the source files however you choose. You will need to use Ant to build the project before running it. Download Ant from https://ant.apache.org/bindownload.cgi. Here is an easy set of instructions on how to setup Ant on Windows: https://www.mkyong.com/ant/how-to-install-apache-ant-on-windows/. Once you have Ant set up, you can build and run the project as follows:
   - On the command line, navigate to the FlashCards directory (the folder you downloaded from GitHub), and type: "ant". This will build the project.
   - From the FlashCards directory, type "cd build/classes" to navigate to the classes directory. Then type "java flashcards.Main" to execute the program.