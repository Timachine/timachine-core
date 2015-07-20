Timachine
=========

Timachine is a general application version management tool. It is designed for managing changes of all kinds of data besides source code when doing version upgrading & downgrading.

All web applications generally face data migration problems during version upgrading, such as database schema & content, files, and configurations. When there are multiple instances of application, e.x. every developer has his/her own PC that running an instance during development, the management of changing those data is really important to keep a unified, consistent application and development life-cycle.

Basically, Timachine borrows the concept of `Ruby on Rails Active Record Migration`_.





Modules
-------

There are 2 modules inside `timachine project`_:

* timachine-core
    The core library of Timachine. It defines the execution logic of abstract migrations, and provide interfaces for outside to implement concrete type of migrations, such as ``VersionProvider``, ``TransactionManager``.
* timachine-maven-plugin
    Maven plugin for easily execute timachine functions.

Migrations
----------

Migrations defines the process during upgrading & downgrading, each migration generally defines a **up** method and **down** method. The principle is that "down" method revokes the change made by **up** method.

The execution process of migrations can be described as an example:

We have migrations M1, M2, M3, M4, M5, the current version is M3. Once we trigger migration process, it executes:

.. graphviz::

   digraph up {
      rankdir=LR;
      node [style=filled,color="#2980B9", fillcolor="#2980B9"];

      M3 -> M4 [label = "M4 (up)"];
      M4 -> M5 [label = "M5 (up)"];
   }

By default Timachine will execute migrations towards the newest version. However, we can explicitly set the ``to`` version. If we set ``to`` as M1, it executes:

.. graphviz::

   digraph down {
      rankdir=LR;
      node [style=filled,color="#2980B9", fillcolor="#2980B9"];

      M3 -> M2 [label = "M3 (down)"];
      M2 -> M1 [label = "M2 (down)"];
   }

Migration Files
"""""""""""""""

Timachine recognize a Java class as a migration when it is annotated with ``@Migration``. Let's see an example:

.. code-block:: java

    package com.worksap.timachine.migrations;

    import com.worksap.timachine.model.Down;
    import com.worksap.timachine.model.Migration;
    import com.worksap.timachine.model.Up;

    @Migration
    public class M20141106173500CreateTable {

        @Up
        public void createTable() {
            //Do something to create the table
        }

        @Down
        public void dropTable() {
            //Do something to drop the table
        }
    }

This migration defines the creation & deletion of a DB table.

There are several constraints on a migration file:

#. The class should be annotated with ``@Migration``
#. The name of class should start with "M" or "T", followed by a time formated as "yyyyMMddHHmmss", and an optional name starting with up case letter.
#. The class should have and only have exact 1 **public non-static** method annotated with ``@Up``
#. The class may have **at most** 1 **public non-static** method annotated with ``@Down``

.. warning::
   Migration files should not be changed anymore once published(Git merged). Changes on merged migration files will cause data inconsistency since the instances which have executed it won't regard this change. And the **down** method will mismatch with the already executed old **up** method.

   If supplement is needed for one published migration file, create new migration files for it.



Revocable & Irrevocable Migrations
"""""""""""""""""""""""""""""""""""

A single migration file is revocable if it defines the **down** method, otherwise it is irrevocable.

A migration execution is revocable if all migration files are revocable, and is irrevocable if any one of the migration files is irrevocable.

**Timachine will give warning if you try to do irrevocable migration. And it will not be able to migrate down if the migration file of any one step doesn't implement down method.**

.. caution:: Irrevocable migration is designed only for de-facto revoke-impossible changes. Always implement **down** method if possible!



Plugin Usage
------------

Maven setting
"""""""""""""

Add this plugin into ``pom.xml``

.. code-block:: xml

    <build>
        <plugins>
            ...
            <plugin>
                <groupId>com.worksap.timachine</groupId>
                <artifactId>timachine-maven-plugin</artifactId>
                <version>1.1.2</version>
                <configuration>
                    <packageName>com.worksap.timachine.migrations</packageName>
                    <testPackageName>com.worksap.timachine.testmigrations</testPackageName>
                    <templateName>migration</templateName>
                    <executor>com.worksap.timachine.SomeExecutor</executor>
                </configuration>
            </plugin>
        </plugins>
    </build>

Configurations:

#. packageName
    The package location storing migration files.
#. testPackageName
    The package location storing migration files in test sources.
#. templateName
    Name of the template of migration files.
#. executor
    The implementation class of an ``Executor`` for executing migrations.

Goals
"""""

Use ``mvn timachine:help`` to list all goals and their usage.

.. _Ruby on Rails Active Record Migration: http://api.rubyonrails.org/classes/ActiveRecord/Migration.html
.. _timachine project: https://github.com/Timachine/timachine-core
