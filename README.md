# Dead Letter Exchange - Rabbitmq

In this article, I want to explain dead letter exchange quite simply.

What is rabbitmq and dead letter queue? 

Rabbitmq is open source message broker that provide to communication between applications.

What is Dead Letter Exchange and why do we need to use? 

Dead Letter Exchange is a exchange type that allow get and process again same message when can not processing message due to some reason.
There are 3 reasons for occur this case.
* Negatively acknowledged = The message is negatively acknowledged by a consumer using basic.reject or basic.nack with requeue parameter set to false. 
* TTL = The message expires due to per-message TTL.
* Length limit = The message is dropped because its queue exceeded a length limit.

<br>
<img src="https://github.com/melihhtasci/dead-letter-exchange/tree/main/doc_images/dead-letter-exchange.png?raw=true" alt="image" width="500"/>

## Getting Started

Imagine that students applied to college. Some of students will be accepted.
And there are some rules to process these applications.


I runned Rabbitmq on Docker

>docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.9-management

In my case, I will send students information to queue. Queue will has max message limit and ttl.

```
    public static final String QUEUE = "STUDENT_APPLICATION_QUEUE";
    public static final String EXCHANGE = "APPLICATION_EXCHANGE";
    public static final String ROUTING = "APPLICATION_ROUTING";

    public static final String DLX_QUEUE = "DLX_STUDENT_APPLICATION_QUEUE";
    public static final String DLX_EXCHANGE = "DLX_APPLICATION_EXCHANGE";

    private final Map<String, Object> ARGS = new HashMap<>()
    {
        {
            put("x-max-length", 5);
            put("x-message-ttl", 60 * 1000);
            put("x-dead-letter-exchange", DLX_EXCHANGE);
            put("x-dead-letter-routing-key", ROUTING);
        }
    };
```
Here is informations of queues. Other definitions are continuing in QueueDeclarations.class.

I set the maximum message count of queue to 5 and time to live value to 60 seconds.
You can change if you want to see results quickly.

I created Student.class to give some information to student.
Then created **Consts.class** to use some const while creating new student. 

```
@Data
@ToString
public class Student {

    public int id;
    public String name;
    public String surname;
    public String nameSurname;
    public Boolean availableToApplication;
    public String applicationDate;

    public Student() {
        this.id = (int) (Math.random()*(maxId - minId + 1) + minId);
        this.name = names[new Random().nextInt(5)];
        this.surname = surnames[new Random().nextInt(5)];
        this.nameSurname = this.name + " " + this.surname;
        // set true if you dont want to see else condition that messsage has not send
        this.availableToApplication = new Random().nextBoolean();
        this.applicationDate = LocalDateTime.now().toString();
    }
}
```
One left step to complete task.

To periodically create student, send to queue and handle dead letter exchange, 
I will use **@Schedule** annotation.

I defined a variable to check that is DLQ empty.
``
boolean isDeadLetterQueueEmpty = true;
 ``

The following method will check waiting message count on dead letter queue
and set variable **isDeadLetterQueueEmpty** to true or false. It will run per 10 seconds.

```
    @Scheduled(fixedRate = 10 * 1000)
    public void checkDeadLetterQueue() {
        Properties properties = amqpAdmin.getQueueProperties(DLX_QUEUE);
        Integer messageCount = (Integer) properties.get("QUEUE_MESSAGE_COUNT");
        // 5 is random limit to see the case
        if (messageCount > 5)
            isDeadLetterQueueEmpty = false;
        else
            isDeadLetterQueueEmpty = true;

    }
```
Following method will send message to queue if message is suit. What are our conditions?

* Dead Letter Queue must be empty. (I set message count to 5. You can change it 0)
* Student must be accepted

This method will run per 5 seconds. 

```
    @Scheduled(fixedRate = 5 * 1000)
    public void send() {

        if (isDeadLetterQueueEmpty) {
            
            Student student = new Student();
            System.out.println(">>> Student  is " + student);
            
            if (student.availableToApplication) {
                System.out.println("Welcome " + student.nameSurname + "-" + student.getId());
                rabbitTemplate.convertAndSend(ROUTING, student.toString());
            } else
                System.out.println(" Sorry " + student.nameSurname + " :(");
        } else
            System.out.println("!!! Process Dead Letter Queue !!!");
    }
```

I just coded situation and behavior of Dead Letter Exchange, not other things. For now at least. 

Anyway that's all. 


### Reference Documentation

* [Rabbitmq](https://www.rabbitmq.com/dlx.html)
* [Spring for RabbitMQ](https://docs.spring.io/spring-boot/docs/2.7.4/reference/htmlsingle/#messaging.amqp)

