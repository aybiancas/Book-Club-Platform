# Book Club Platform

#### A simple Java desktop social app for managing a book club - members and managers can explore books, engage with posts and comments and manage their occasional meetings.

### Roles

The system has two main roles: Managers and Members. 
Members are regular club participants, they can search for books in the database, manage their own reading lists, read and comment on posts and attend meetings.
Managers are administrators. In addition to what the regular members can do, they also oversee the other members of the club, can add and remove books and venues from the database, register or remove other users, schedule and cancel meetings and publish posts on the feed.

### Features

#### Books and reading lists

The club has a collection of books, each assigned to a category. Members can add books to their own reading list, and managers can add or remove books from the collection.

#### Meetings

Managers can schedule meetings around a specific book from the collection at a certain date and time. The system checks whether two meetings are trying to be scheduled within one hour and does not allow that. After a meeting was added or when an existing meeting that has been marked by an user is edited or cancelled, the user will get a notification.

#### Venues

Venues are necessary for the meetings. They can be one of two types: physical (with an address) or online (with a link). Managers can add, edit or remove venues.

#### Posts and comments

Posts can only be posted by managers on a club-wide feed and are open to discussion and announcements. Any user can leave comments under the posts.

#### Notifications

Users can get automatic in-app notifications when a meeting is scheduled, rescheduled or cancelled. Unread notifications will be highlighed and the user has the choice to mark all notifications as read.


### Structure

The app was written using Java 25, the GUI uses JavaFX with FXML and controllers.
The model was migrated to a PostgreSQL database.

### Database

Below is the ERD diagram of the database structure

![ERD diagram](/other/erd_diagram.png)

