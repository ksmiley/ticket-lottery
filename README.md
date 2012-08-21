# Web-based Ticket Lottery

A web-based program for lottery-style distribution of a limited supply of tickets (typically for a sporting event) to a group of participants. This was my final project for CS499, which was the capstone course for all senior computer-science students at the University of Kentucky.

The final team that my team turned in with the project is available at <http://keithsmiley.net/lotto-report/>.

## Background

The University of Kentucky has a highly successful basketball program that's popular nationwide but even more so on the main campus. The demand for student tickets outstrips their availability, so those tickets are distributed using a lottery system. In 2009 when I was completing the capstone class for a computer science degree, the lottery was still done in-person and in real-time. Every few weeks on a Tuesday evening, thousands of students lined up outside the old arena on campus, collected a number as they entered, and waited in the bleachers (sometimes for hours) hoping their number would be called. Many students could not participate because their classes or other obligations overlapped with the lottery.

Our class decided to use the ticket lottery as the semester-long project. Each team in the class would design an online lottery system to make the process more fair by allowing wider participation. The goal of the class is to produce software that can actually be used by the real-world customer.

Requirements gathering started well, but by midway through the semester the project faced significant problems. Staff in the athletics department made it clear they had no intention of changing the lottery, so we did not actually have a customer to work for. Furthermore, the student responses from a short online survey we ran showed that many lottery-goers did not want the process to move online. They perceived the existing lottery as requiring enough sacrifice that it discouraged participation by students who only half-heartedly wanted tickets or were unlikely to use them.

At that point some of the teams in the class petitioned to change to different projects. The other teams that chose to continue were instructed to make their implementations generic with the hope they could be used by other schools. Our team continued with the project by removing all UK-related branding and focusing on parts of the interface that would apply to any customer.

## Design

The original, physical lottery was limited in how frequently tickets could be distributed because the process required taking over a large building for several hours, plus more hours for planning and organization. Because of the overhead, only a few lotteries were held during basketball season, and each lottery distributed tickets for multiple games.

With less overhead, an online lottery removes the need to group multiple events into a single ticket distribution. Our system allows for a rolling lottery where each event is handled separately. The process for each event is split into phases:

* __Not Open:__ The event is on the schedule but is too far in the future for registration.
* __Register:__ Participants can register for the event's lottery, either as individuals or by forming small groups.
* __Claim:__ Winners must claim and pay for their tickets during this window or the tickets are put up for sale. (All available tickets are assigned to participants during the state change from __Register__ to __Claim__.)
* __Sales:__ Tickets that went unclaimed or that were canceled by the winners are made available for purchase by non-winners.
* __Closed:__ Tickets can no longer be purchased or canceled because the event is about to start, in progress, or already finished.

The length of each phase can be set differently on each event, which gives administrators flexibility in scheduling around holidays and weekends.

The management section of the application also includes screens for setting up seating charts for multiple venues and for quickly assigning blocks of available seats to each lottery event.

## Missing Pieces

The existing code implements most of the interfaces needed for admins to set up a lottery and for users to enter lotteries. Some screens, such as the ones that let users create and join groups so they can sit together at the event, need improvements but are implemented.

However, there is no user management because we expected it to be integrated with an external directory. For the demo we created users directly in the database.

There are also no ticket purchasing screens because we expected most of the work to be handled by an external vendor.

Most importantly, there is no ticket distribution, because we didn't get to it in time. We also did not have much guidance on what logic to use or what factors to consider when choosing whether a specfic user gets tickets. For example, if a user has won before, should that person be eligibile to win more tickets, disqualified from the next lottery, have their probability of winning reduced, or something else? What if one member of a group won previously but the others had not?

* * *

## Prerequisites

Running the lottery system requires a servlet container (tested with Tomcat 6) and a MySQL server with InnoDB enabled (tested with 5.1).

Building the project form source requires several libraries (with version numbers tested):

* [Spring MVC](http://www.springsource.org/) 2.5
* [Spring Security](http://static.springsource.org/spring-security/site/) 2.0.5
* [Hibernate](http://www.hibernate.org/) 3
* [Spring JSON](http://spring-json.sourceforge.net/) 1.2.1 (used for JSON formatting)
* [SOJO](http://sojo.sourceforge.net/) 1.0.0 (used for JSON parsing)
* [Apache Commons Collection](http://commons.apache.org/collections/) 3.2.1

The client-side code makes heavy use of jQuery and jQuery UI, but those libraries are included under `web/assets/`.

## Installation

The project was built with Netbeans, so there is a generated Ant build.xml file included. The build spec should work without Netbeans but will need to be updated to point to the proper location for libraries.

Building the project will produce a TicketLottery.war file containing all of the application code, JSP templates, JavaScript, etc. Deploy the file to your servlet container of choice.

Create a MySQL database and load the schema dump in the `db/` directory. Put the connection credentials into the `jdbc.properties` file.

The schema file will create a default administrator with the username 'admin' and password 'admin'. Use these to login to the admin section under `/manage/`.

The lottery-distribution system is really only useful if integrated with an external user database and a ticketing vendor. Out of the box, there is no support for external systems.