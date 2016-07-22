
## 0 # Welcome to the Scalanator Akka Course

*Note that this course is under development and the text and exercises may be incomplete*

Welcome to the Scalanator.io Akka course.

This part of the course will give you with the background and experience needed to create effective scalable systems in Akka.

Building large scale asynchronous systems can be a complex business.  It is necessary to master concurrency, carrying out processes at the same time, parallelism, carrying out the same task by separate systems, dealing with faults and errors without compromising the integrity of the system and dealing with processes distributed across different systems.

In Akka these problems are handled with ‘Actors’, a technical term used within programming.  In simple terms an Actor is an isolated process that takes in, processes and transmits data in a predefined way.


## 100 # Building a mental model - Introduction

The key concepts of the Actor model can be understood through an analogy:

Welcome to Widget Inc.

(Picture of office block)

Think of a mail order company before the days of the internet.  Customers would send in their orders and requests for information to the factory by letter.  The letters would be processed within the factory and completed orders for the product despatched by mail to the customers.

An incoming letter might be in the form:

* "Please tell me the dimensions of your Widget"
* "Please tell me the price of 3 widgets"
* "Please send me 1 widget"

## 110 # Building a mental model - The simplest approach

Suppose that Widget Inc is a one man band.  Our sole worker processes incoming orders by working through his instruction list:

1. Wait for a letter
2. Read the letter
3. Action the letter
4. Reply to the letter
4. Repeat from step 1.

As long as the worker can keep up with the number of letters being received there the process proceeds smoothly.

However some tasks may take a long time.  If a cheque is received the worker must go to the bank to cash it before the goods can be sent out.  While the worker is cashing the cheque the ordering process is at a standstill until the banking process has been completed.

## 120 # Building a mental model - The simplest approach - Speeding up

Suppose that the business grows and our one worker cannot keep up with the flow of orders. How do we fix this?  Add more workers!

Each of our new workers follows the original set of instructions and each picks up incoming mail from a shared pile.  In theory doubling the number of workers, doubles the throughput.

If the company receives two widget orders, then two workers will:

1. Check there is a widget in the warehouse
2. Go to the bank and cash the cheque
3. Collect the widget and send it to the customer

Suppose that two orders come in at the same time and there is only one widget in the warehouse.  Two workers independently process their orders, both check the warehouse stock, confirm that one is in stock, and then go to the bank to cash their cheques.

The first worker to return picks up the widget from the warehouse and sends it to the customer.  The second worker gets to the warehouse and is stuck, he has taken payment, but there is no widget to send.

## 130 # Building a mental model - The simplest approach - Complexity attacks

The simple model with multiple workers acting independently has problems as many of the tasks need some interaction with other workers.  As the number of workers increases so does the complexity.
In our example with two workers we could have a warehouse key.  The operating instructions now include:

1. Get the key to the warehouse
2. Check the stock level
3. Process the payment
4. Get the item from the warehouse and send it to the customer
5. Return the key

Since there is only one warehouse key we still only handle one sales order at a time, no matter how many workers or widgets we have.  If one worker has the key then the others are standing idle as they can’t proceed beyond step 1 of their instructions.    We have solved one problem (preventing the same widget being ordered twice) but introduced another.  More importantly, doubling the number of workers has not doubled the work output.



## 140 # Building a mental model - Fighting the complexity

The underlying problem may be solvable, for instance, the worker could take the key, go into the warehouse, check the item, reserve it, return the key and then go to the bank.  The problem is that the task complexity increases exponentially.

Our workers are pedantic, they will follow the instructions they have been given exactly without introducing errors of their own.  But they will give up if their instructions do not exactly cover what they are to do next.  For instance, in the example above, what happens if the warehouse is empty or the bank is closed.  Except for very simple tasks it may not be possible to write instructions that are completely clear and correct as well as covering all circumstances.

How can we as writers of instructions meet these criteria?  We must find a simplification that can be applied, bearing in mind that any simplification is a tradeoff between strengths and weaknesses.

We are going to use a solution that goes back to the heart of efficient production in the Industrial Revolution - specialisation.

## 150 # Building a mental model - Fight the complexity - Rebuilding our factory

In the real world we face two problems, the ability to write correct and complete instructions and the ability of our workers to follow them.

In our imaginary world we only have to deal with the first problem in our factory since we know that our workers lack independent action and will slavishly follow whatever set of instructions they are given.

So how do we create sets of instructions to meet all the requirements?  The answer is isolation and specialisation.   We are going to break things up and add some rules to make dealing with the problems easier.

## 160 # Building a mental model - Fight the complexity - Rebuilding our factory 2

To return to the factory analogy, we hire more workers but make each worker’s job more focused.  Rather than having each worker responsible for the whole order we are going to hire workers who each deal with different parts of the ordering process.


## 200 # Building a mental model - The new design

Our new factory is going to go even further, every worker is going to be put in their own office.  Additionally they will only be allowed to communicate with other parts of the factory by using a new message system.

This is the general arrangement, the next section describes in more detail the different parts the new factory design and how they operate and interact.  Some of the choices or rules may seem rather arbitrary at this point but we will deal with the underlying explanations later.

## 210 # Building a mental model - The new design - Messages

The first rule is that each worker can only communicate with other workers through a shiny new pneumatic message tube system.

Message tube systems exist in the real world, for example [UMHS Pneumatic message system](https://www.youtube.com/watch?v=gAlzYLcqsTU)

Messages gain some key properties:

1. A message can only go to a single recipient
2. A message cannot be changed once it has been sent
3. The order of messages from a given sender to a given receiver is always maintained

## 220 # Building a mental model - The new design - Workers and Offices

Every office has an incoming message tube, an outgoing message tube, a pad of paper for keeping notes and an instruction manual.

Each worker sits in their office waiting for messages. When they receive a message they read it then use their instruction manual that shows them what action to take.

For example they could:

1. Do nothing and discard the message
2. Update some information on their pad of paper
3. Send new messages to others
4. Forward the message to somebody else
5. Some combination of these

Each worker processes a one message at a time and ignores further incoming messages until that message has been handled.

## 230 # Building a mental model - The new design - Is it better?

This new layout seems like a lot of work and set up for no obvious immediate gain.  The key benefit (and the key benefit of all design patterns) is the ability to reason about each part of the design in isolation.

Now, when thinking about the warehouse, we only need to worry about 'can I reserve X' or  ‘please dispatch Y’ without having to deal with billing or any other aspect of the business.  In the warehouse we only need to know:

* The current state (what stock we have)
* The current message we are processing


## 270 # Building a mental model - Managing workers

The workers in our factory are highly strung.  If at any time they arrive at a situation not covered by their instructions they trash their office and walk out.  No matter how careful we are with the instructions it is always possible that we have missed something that could cause a failure in the process, so how do we handle this?

We handle this issue through supervision.  Each worker is hired and and managed by a single supervisor.  If the worker quits, the supervisor is informed.  The supervisor may then do one of several things:

1. Accept it and continue
2. Hire a replacement worker
3. Quit themselves

Everyone has a supervisor, including the supervisor.  Each of the higher supervisors may also have a supervisor above them.  Unlike a factory in the real world there is a single chain of supervisors above each worker, no supervisor oversees more that one worker and only reports to one supervisor above.  If a supervisor quits for any reason the supervisors below them and the worker at the bottom are summarily fired.

At the very top there is a special ‘company guardian’ supervising all the chains of supervisors and workers.  If a supervisor at the top of a chain quits then the guardian take a number of actions which can include shutting down the entire company.


## 250 # Building a mental model - Expensive workers

Let us imagine for a moment that workers are expensive and overheads need to be reduced without affecting the running of the company.

With the previous model, where each worker has their own office, workers will be idle a lot of the time as they sit in their office waiting for a new message.   Let’s fire them all workers but keep their offices and their contents.  We will employ a single worker to  rove around the building, looking for offices with messages waiting.  If he sees one, he pops in, uses the instructions in the office to handle the message and then goes back to roaming the building.

When you think about this all of the work will still get done.  No two pieces of work are done at the same time (it might be slower), but the final outcome is exactly the same.

This is 'concurrency', we are 'dealing with a lot of things at once', without getting confused or losing track.


## 260 # Building a mental model - Expensive workers 2

To speed things up we could hire more workers to roam the building.  This is 'parallelism', multiple tasks are being carried out simultaneously.

It is worth keeping these two goals in mind, dividing work into chunks that can be done simultaneously and increasing throughput by doing them at the same time.

In most cases it is easier to think about a single worker working in a single room.

## 240 # Building a mental model - Why this model works

This model achieves two things, firstly it is much easier to reason (better for us) about and secondly we can have workers working in parallel.

For tasks that are independent the workers can perform their jobs simultaneously.  For example a billing a customer and sending an order.

Work that needs to be coordinated (such as two order reservations in the warehouse) are processed in a consistent way by a single worker.

## 500 # Moving to the real world

## 505 # Our program

The program we are creating is our factory.  To create the factory we define

## 510 # Actors - Introduction

In Akka parlance each worker is an Actor.  Actors are simply classes that extend the class ```Actor```.  The only requirement is that you implement the ```receive``` function.  Each message sent to your Actor’s mailbox is handed to this function in turn.    You can think of this as a single thread performing this action:

```
while(true) {
	val message = mailBox.take() // block if none ready
	Actor.receive(message)
}
```

This means that within the ```receive``` function you do not need to worry about multi-threading at all.  However, if you block within your Actor's ```receive``` block you will not have any more messages delivered.

## 515 # Actors - Our first Actor

Let us implement our first Actor.  When this Actor receives a  ```SayHello``` message it will log the String “Hello name” - where the name comes from the message.
If it receives any other message it will simply log that it did not recognise the message.

```{.scala .numberLines }
import akka.Actor.{Actor, ActorLogging}

case class SayHello(name: String)

class HelloActor extends Actor with ActorLogging {

  def receive = {
    case SayHello(name) => log.info(s"Hello $name")
    case _      => log.info("received unknown message")
  }
}
```


## 550 # Actors - Introducing ```ActorRef```s

An Actor is just a class, which means if we had a reference to a given Actor we could easily break encapsulation and thread safety by directly accessing the Actors methods and variables.

In Akka we never deal with an Actor directly, but only by its handle.  The ```ActorRef``` is an Actor’s 'address card'.  In our mental model you could think of this as the assigned office number.  You can only send someone a message if you know where they are.

In Akka you never construct an ```ActorRef``` yourself.  There are three primary ways to obtain a reference:
1. When you ask Akka to construct an Actor (we will cover this later), you are returned the ```ActorRef```.
2. Within an Actor you can see ```self``` which is your own Actor reference and ```sender``` which is the address of the message sender of the current message.
3. An Actor can also see the ```ActorRef``` of its parent, and any children it has created.
3. You can received an ```ActorRef``` in a message.

## 560 # Actors - Sending messages

All inter-Actor communication is through message. To send an Actor a message by using the ```ActorRef``` using the ```!``` method:

```scala
  ref ! message
```

Remember, this is asynchronous, but one extra rule is applied:

```scala
  ref ! message1
  ref ! message2
```

```message1``` will be delivered to ```ref``` before ```message2```


## 570 # Actors - Your turn

For this exercise, update the Actor to send back the received message to
the sender:

@:editor file=exercises/first/FirstActor.scala

## 575 # Actors - Creating Actors with Props

One thing that might seem strange at first is that you do not type:

```scala
val myActor = new FirstActor()
```
Instead you create a Props object such as:
```scala
val myActorRef = system.ActorOf(Props(new FirstActor()))
```
outside an Actor or:
``` {.scala .numberLines }
val myActorRef = context.system.ActorOf(Props(new FirstActor())
```
inside an Actor.

A ```Props``` is a definition of an Actor - it is a immutable, shareable factory for creating instances (we will do more with this feature later).
Whilst it feels long winded this pattern helps us do several things:

1. Hides the actual object reference (we only want the ActorRef).
2. Allows sharing of creation patterns (more on that later)
3.  Maintains the supervisor/child relationship

## 580 # Actors - Creating Actors with Props 2

There are three ways of creating a ```Props``` instance:

### No parameter type creation
```scala
val props = Props[FirstActor]
```

### Using a type constructor
```scala
val props = Props(classOf[ActorWithParams], "param1", "param2"...)
```
One problem with this constructor is that is is not typesafe, the parameters are only checked via reflection at runtime.

### Using a type constructor

```scala
val props = Props(classOf[ActorWithParams], "param1", "param2"...)
```

### Using a lazy constructor

XXXX REWORK THIS - lazy constructor

```scala
val props = Props(new ActorWithParams("param1", "param2"...))
```
This is our preferred way of constructing Actors, however it does have a dangerous side.

## 590 # Actors - Creating Actors with Props 3

If you call
```scala
val props = Props(new ActorWithParams("param1", "param2"...))
```
Within an Actor you can risk closing over the Actor’s internal state (i.e. granting access to an immutable internal variable which should not have been exposed).

A simply pattern prevents this:

```{.scala .numberLines }
import akka.Actor.{Actor, ActorLogging, Props}

case class SayHello(name: String)

class GreetActor private (greeting: String) extends Actor with ActorLogging {

  def receive = {
    case SayHello(name) => log.info("$greeting $name")
    case _      => log.info("received unknown message")
  }
}

object GreetActor {
  def props(greeting: String): Props =
    Props(new GreetActor(greeting))
}
```
Marking the constructor private and pushing the construction of the Props into a method on the companion object means that people cannot accidentally close over state, but maintain compile time type safety.


## 1000 # Sieve - The Sieve of Eratosthenes

To give you a sense of build systems out of Actors we are going to build a small application.


## 1010 # Sieve - The 'Sieve of Eratosthenes'

A very simple way to find prime numbers from the list of all integers is through sieving.  Each sieve is a set of instructions that weeds out non-prime numbers.

A prime number is defined as a number that is only divisible by itself and 1.

Each sieve will remove any number exactly divisible by the first number it sees.


The first number the first sieve sees is 2.  This is a prime number, the only even prime number as any other even number will be divisible by 2 so, by the definition, cannot be a prime number.  The first sieve is the 2 sieve that will move along the list of integers removing any number which is an exact multiple of 2 (4,6,8 etc).

When this process has been completed the next sieve number is advanced by 1 so it will set number 3 as a prime and sieve out exact multiples of three.  That is, it sees and removes 3,9,15 and so on.  It will not see 6 and 12 even though they are multiples of 3 because they have already been removed by the previous sieve.

The next sieve will be the 5 sieve as 4 has already been removed.

Each sieve will begin seeing a prime number and then will remove any multiples of that prime number.

We are going to build an application which implements an Actor based sieve to find the first N primes and explore some Akka features along the way.

## 1020 #  Sieve - The source

Our first task is to create a number source to provide the next integer in the sequence to the requestor.

A formal definition of the Actor is as follows:

* Take a given initial seed
* Each time a 'Next' message is received reply with the next number in the highest integer (starting with the seed)

Here is our Actor outline

``` {.scala .numberLines }
package sieve

import akka.Actor.{Actor, Props}
import sieve.NumberSource.Next

object NumberSource {
  def props(start: Int): Props = Props(new NumberSource(start))

  case object Next
}

class NumberSource private (seed: Int) extends Actor {

  private var next = ???
  override def receive = {
    case Next =>
        ???
  }
}
```

This follows the template from the previous example.

## 1030 # Sieve - Black box testing

Let us do a little bit of test driven development, TDD for short.  One of the facets that make Actors so compelling is their inherent testability.
In most cases (except, for example, when it is wrapping underlying IO) a well designed Actor has external interactions other than the messages it receives, sends and the child Actors it creates.

Akka comes with the Akka TestKit, a set of test utilities that make it easy to exercise an Actor’s behaviour.
Below is a simple test of the Number source (our testing would generally be a little more extensive than this ;) )

Take a look at the following code:

``` { .scala .numberLines }
val numberSource = system.ActorOf(NumberSource.props(10))
numberSource ! NumberSource.Next
expectMsg(10)
numberSource ! NumberSource.Next
expectMsg(11)
numberSource ! NumberSource.Next
expectMsg(12)
```

This code is executed within a test harness that will be covered in the next lesson.  By default the test kit uses itself as the message sender (the ```sender``` you will reply to).

On line 1 we create an instance of the Number Source class.
One line 2 we send the number source the ```Next``` message (defined on the ```NumberSource``` companion object)
One line 3 we wait a message in reply (in this case 10).
We then send ```Next``` again expecting to receive the next number in the sequence.


## 1040 # Sieve - Implementing the NumberSource - your turn

You task is to now implement the ```NumberSource``` behaviour


@:editor file=exercises/createns/NumberSource.scala


## 1050 # Sieve - Creating the sieve

So let us define our Sieve Actor.  The sieve Actor:

* Takes a ```target``` to which to send primes and requests for more numbers.
* Publishes the first number it sees as a prime, using the message ```FoundPrime()``` and creates a new child sieve.
* From then on filter all remaining numbers
  * If it is exactly divisible by ```prime``` sends ```Next``` to the ```target``` to request a new number.
  * It is is not divisible forward to the next sieve (child).

## 1060 # Sieve - Immutability in Actors and becoming

The ```Sieve``` Actor has two distinct states, before and after it has seen its first number.
We can model this in two ways, firstly we could have mutable variables for the prime number and the child reference (both of which need to handle subsequent numbers) or we can use the ```become``` method.

The simplest Actor says

``` {.scala .numberLines}
override def receive: Receive = {
    case x => // do something with x
}
```

become allows us to substitute an alternative receive method which can take parameters. As an example:

``` {.scala .numberLines}
def gotPrimeReceive(prime: Int): Receive = {
    case x : Int =>

}
```

To switch incoming messages to use the method we simply call:

``` {.scala .numberLines}
context.become(gotPrimeReceive(p, true)
```

The second parameter allow us to handle receives as a stack, pushing and popping receive handlers - in our case we simply want to ```become``` a new state and discard to previous one.

## 1060 # Sieve - Testing Actors that create children

Testing the behaviour of Actors that create children is fiddly.  If the parent creates an child by simply:

```scala
context.ActorOf(Sieve.props(target))
```

XXXXXX

## 1070 # Sieve - Your Turn

@:editor file=exercises/createsieve/Sieve.scala


## 3000 # Random Stuff

## 3010 # Futures and closure over state

TODO Talk about closing over state and moving future work outside of the Actor into companion to avoid.


## 4000 # Rules for Actors

### 1. Only use immutable messages

Passing a mutable state in messages is the road to madness.  By not obeying this cardinal rule you have thrown away all of the safety the Actor system has built.
We recommend you use case classes and immutable data structures for all messages.
The one exception we generally make for this is the passing of a ```Promise``` to allow results to be shared in multiple places.


### 2. Never Block your Actor

Putting a blocking call in your Actor means that it is unreachable, it will not receive any other messages until the blocking call has completed and cannot be interrupted.

Use futures, ```become``` and messages to avoid blocking (e.g. doing an ```ask```)

### 3. Never let state leak out of your Actor

To maintain the model’s integrity do not expose any internal state of your Actor outside of the Actor.  Avoid:

1. Creating ```Props``` of a child directly in your class, see the discussion in the ```Props``` section.
2. Be careful with ```future.onComplete``` and its friends - code in the completion block is executed in the futures thread space, not on the Actor thread, meaning you exposing Actor state outside the Actor.

## 9999 # Fin

You made it - thanks for coming!

We hope you have enjoyed this course - we would love to hear your feedback.
