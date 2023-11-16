Web Crawler Application

Description:

The Web Crawler Application is a Java-based utility designed for the recursive crawling of websites, facilitating the downloading and local storage of both pages and associated resources. This application leverages multithreading to enhance crawling performance and employs pre-order traversal for optimal memory usage.

Key Features:

Multithreading: 

The application utilizes a thread pool to execute crawling tasks concurrently, enhancing overall performance.

Resource Handling: 

It can download and save web pages as well as associated resources such as images, links, and scripts.

Error Handling: 

The code is designed to handle exceptions thrown during the crawling process, providing improved robustness.

Configurability: 

The number of threads, output directory, and starting URL are configurable parameters, offering flexibility in usage.

Code Design:

The code is structured around three main components:

Crawler Class: 

Responsible for initiating and coordinating the crawling process. It utilizes a thread pool for parallel execution of crawling tasks.

PageReader Interface: 

Defines the contract for classes responsible for reading web pages. The HTTPPageReader implementation uses Jsoup to connect to a URL and retrieve the document.

FileWriter Interface: 

Specifies the behavior of classes responsible for writing content locally. Two implementations, BlockingFileWriter and NoneBlockingFileWriter, demonstrate synchronous and asynchronous writing strategies.

Testing:

The application includes unit tests using JUnit 5 and Mockito. The tests are located in the src/test directory. To run the tests, execute the following Maven command in the project root directory:

Test Coverage:

CrawlerTest

This test validates the functionality of the Crawler class. It mocks the PageReader and FileWriter dependencies and tests the start method, ensuring that all endpoints are extracted as expected, and the crawler finishes successfully.

Dependencies:

JUnit: For unit testing.

Apache Commons CLI: For parsing command line arguments.

Jsoup: For HTML parsing and manipulation.

Apache Commons IO: For file and stream operations.

Building and Running:

Clone the Repository: 

git clone https://github.com/bjuhret/tretton.git

cd web-crawler

Build the Project: mvn clean install

Run the Application: 

java -jar target/web-crawler.jar -t 5 -a (Replace -t with the desired number of threads and -a for asynchronous downloading.)

Limitations and Future Considerations

Out of Memory Issues

There are scenarios in which the application may encounter out-of-memory problems, particularly when dealing with very deep and wide link graphs combined with limited hardware resources. One potential solution for such scenarios is to implement a write-ahead strategy, where all links are initially written to disk to prevent the storage of future scheduled work in memory.
However, it's important to note that the current design decision does not incorporate this strategy. The additional complexity introduced by this approach has not been deemed justifiable for the identified edge cases. As the application evolves, future optimizations and enhancements may explore alternative strategies to address memory-related challenges.

Test Coverage

While the application contains a basic test to verify critical functionality, it's essential to acknowledge that comprehensive test coverage is limited due to time considerations.
Recommended Additional Tests
To enhance the robustness of the application, I would consider adding tests for the following scenarios:
Concurrency Testing: Although the application employs multithreading, additional tests focusing on concurrency scenarios can provide insights into the application's behavior under various conditions.
Error Handling: Incorporating tests for error-handling scenarios, such as network failures or unexpected content, would further strengthen the application's resilience.
Integration Tests: While the current test suite emphasizes unit testing, introducing integration tests that simulate end-to-end scenarios could uncover potential issues in the application's interactions with external components.


