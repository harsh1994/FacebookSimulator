package com.example

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import akka.io.IO
import spray.routing.SimpleRoutingApp
import akka.actor.ActorSystem
import java.util.HashMap
import scala.collection.immutable.List
import akka.actor.Actor
import scala.concurrent.duration._
import akka.util.Timeout
import spray.http.HttpEntity
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.can.server.Stats
import spray.http.MediaTypes._
import java.io.FileWriter
import java.awt.Desktop
import java.io.File
import scala.util.Random
import scala.concurrent.duration._
import spray.routing._
import scala.collection.mutable._
import java.math._
import spray.json._
import akka.actor.Props
import spray.client.pipelining._
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import scala.io.Source
import java.util.Calendar
import akka.pattern.ask
import scala.util.Random
import java.io.{File, FileInputStream, FileOutputStream}
import sun.misc.{BASE64Encoder, BASE64Decoder}

case object POSTDETAILS
case object STOP
case object ADDPOST
case object FOLLOWPAGE
case object ADDFRIEND1
case object GET_DETAILS
case object GET_PROFILE
case object PAGEPOST
case object ADDPAGEFOLLOWER
case object ADDPAGEPOST
case object POSTPICTURE
case object POSTALBUM
case object GETALLUSERPOST
case object GETUSERFRIENDS
case object GETALLPAGEPOSTS
case object GETPAGEDETAILS

object Client extends App{

	//	val r = scala.util.Random
	val x : Int = args(0).toInt
			val actor : ArrayBuffer[ActorRef] = new ArrayBuffer[ActorRef]   
					implicit val system = ActorSystem()

					import system.dispatcher

					val pipeline = sendReceive
					/* Post initial details*/
					for (i<- 0 to  x/10)
					{
						actor += system.actorOf(Props(new InactiveUser(i)), "name" + i)
								actor(i) ! POSTDETAILS   
					}

	for (i <-((x/10)+1) to x/2 )
	{
		actor += system.actorOf(Props(new PassiveUser(i)),"name"+i)
				actor(i) ! POSTDETAILS    
	}

	for (i <- ((x/2)+1) to x )
	{
		actor += system.actorOf(Props(new ActiveUser(i)),"name"+i)
				actor(i) ! POSTDETAILS    
	}       

	Thread.sleep(50)

	for(i <- x+1 to x + 1 + x/10)
	{
		actor += system.actorOf(Props(new Page(i)))
				actor(i) ! PAGEPOST
	}

	Thread.sleep(50)


	for (i <- ((x/2)+1) to x )
	{
		actor(i) ! FOLLOWPAGE  

		var r = Random.nextInt(100)
		if(r < 50)
		{
			actor(i) ! POSTALBUM
		}
	}   

	for(i <- ((x/2)+1) to x ){
		actor(i) ! ADDFRIEND1
		//		actor(i) ! ADDPOST
		//		actor(i) ! POSTPICTURE

	}

	for (i<- 1 to  x/10)
	{
		actor(i) ! ADDFRIEND1   
	}

	Thread.sleep(50)

	for (i <-((x/10)+1) to x/2 )
	{
		actor(i) ! GET_DETAILS   
	}

	for(i <- x+1 to x + 1 + x/10)
	{
		actor(i) ! ADDPAGEPOST
	}


	Thread.sleep(50000L)

	for (i <-1 to x)
	{
		var actor = system.actorSelection("user/name"+ i)
				actor ! STOP
	}
} 


class ActiveUser(start:Int) extends Actor
{
	implicit val system = context.system
			import system.dispatcher
			var userid = start
			val pipeline = sendReceive
			val r = scala.util.Random
			//        val a = r.nextInt(100)
			//        var b = r.nextInt(100)
			//        
			//        if( b==a)
			//        {
			//          b = r.nextInt(100)
			//        }

			def getRandomPost() : String =
		{
					val filename = "C:/Users/Harsh/Desktop/Facebook.txt" 
							var array : ArrayBuffer[String]  = new ArrayBuffer
							var count : Int=0
							for (line <- Source.fromFile(filename).getLines()) 
							{

								array+=line
										count+=1
							}
					array(Random.nextInt(count))
		}


			def picture_encode() : String =
				{
					val filename = """C:/Users/Harsh/Desktop/photo.jpg"""
							val file = new File(filename)
					val in = new FileInputStream(file)
					val bytes = new Array[Byte](file.length.toInt)
					in.read(bytes)
					in.close()
					val encodedFile = new File(filename + ".base64")
					val encoded = 
					new BASE64Encoder()
					.encode(bytes)
					.replace("\n", "")
					.replace("\r", "")
					.replace("=", "")
					//			val encodedStream = new FileOutputStream(encodedFile)
					//			encodedStream.write(encoded.getBytes)
					//			encodedStream.close()
					encoded
				}

			def receive = {
			case POSTDETAILS =>
			{
				val result =  pipeline(Post(s"http://localhost:8080/new-user?userid=$start&firstname=Aman$start&lastname=Chanana&dob=07/08/94&gender=M"))
						//				printResponse(result)
						//        Thread.sleep(100)
						//        self ! ADDFRIEND1                
			}

			case ADDFRIEND1 =>
			{        

				var r = Random.nextInt(Client.x)
						if(r == 0){
							r = Random.nextInt(Client.x)}

				if(Client.x >= 1000)
				{
					r = r / 10
				}
				else{

					r = r / 2  
				}


				for( i <-0 to r){
					var b = Random.nextInt(Client.x)
							if(b == 0){
								b = Random.nextInt(Client.x)
							}
					val result = pipeline(Post(s"http://localhost:8080/user/addfriend?userid=$start&friendid=$b"))     
							//					printResponse(result) 

				}
				self ! ADDPOST
				//		self ! FOLLOWPAGE
			}

			case ADDPOST =>

			{ 
				var r = Random.nextInt(100)

						if(r == 0){
							r = Random.nextInt(100)
						} 
				r = r/5

						for(i <- 0 to r){

							val post1 = getRandomPost()
									val result = pipeline(Post(s"http://localhost:8080/user/addpost?userid=$start&post=$post1"))
									//							printResponse(result) 

						}
				self ! POSTPICTURE
				self ! POSTALBUM

			}

			case FOLLOWPAGE =>
			{
				for(i <- 1 to 10)
				{
					val rnd = new scala.util.Random
							val range = Client.x to Client.x + Client.x/10
							val randompage = (range(rnd.nextInt(range.length)))
							val post = pipeline(Post(s"http://localhost:8080/page/addfollower?pageid=$randompage&userid=$start"))
				}

			}

			case POSTPICTURE =>
			{ 


				for(i <- 0 to 4){

					val string = picture_encode()
							val result = pipeline(Post(s"http://localhost:8080/user/uploadphoto?userid=$start&pic=$string"))
							//				printResponse(result) 

				}
			}
			case POSTALBUM =>
			{
				for (i <- 1 to 2)
				{
					val string = picture_encode()
							val result = pipeline(Post(s"http://localhost:8080/user/uploadalbum?userid=$start&albumid=$start&pic=$string"))
				}

			}

			}
			def printResponse(result: scala.concurrent.Future[spray.http.HttpResponse]) {
				result.foreach {
					response =>
					println(s"Request completed with status ${response.status} and content:\n${response.entity.asString}")
				}
			}
}

class PassiveUser(start:Int) extends Actor
{
	implicit val system = context.system
			import system.dispatcher
			var userid = start
			val pipeline = sendReceive
			var abc = Client.x
			var array:ArrayBuffer[Int] = new ArrayBuffer
			
//			val pageid1 = (Client.x + 1) to Random.nextInt(Client.x + 1 + Client.x/10)
			
//			
			for(i<-abc to (abc + abc/10)){
			   array += i 
			}
	val x = Random.nextInt(array.length)
	val pageid = array(x)

			def random() : Int ={
	  				val r = scala.util.Random
						val a = r.nextInt(Client.x)
						var b = r.nextInt(Client.x)

						if( b==a)
						{
							b = r.nextInt(Client.x)
						}
	  				a
	}
			
			def receive = {

			case POSTDETAILS =>{
			  var a = Random.nextInt(Client.x)
				val result = pipeline(Post(s"http://localhost:8080/new-user?userid=$start&firstname=Harsh$a&lastname=Kothari&dob=07/08/94&gender=M"))
						//						printResponse(result)
			}
			case ADDFRIEND1 =>
			{        

				var r = Random.nextInt(Client.x)

						if(r == 0){
							r = Random.nextInt(Client.x)
						}
				if(Client.x >= 1000 && Client.x < 5000)
				{
					r = r / 100
				}
				else if(Client.x <1000){

					r = r / 20 
				}


				for( i <-0 to r){
					var b = Random.nextInt(Client.x)

							if(b == 0){
								b = Random.nextInt(Client.x)
							}

					val result = pipeline(Post(s"http://localhost:8080/user/addfriend?userid=$start&friendid=$b"))     
							//							printResponse(result) 

				}
				//        self ! ADDPOST
			}

			case GET_DETAILS =>{

			  var a = random()
				val result = pipeline(Get(s"http://localhost:8080/userdetails?userid=$a"))
//						printResponse(result)
						self ! GET_PROFILE
			}

			case GET_PROFILE =>{
			  var a = random()
				val result = pipeline(Get(s"http://localhost:8080/userprofile?userid=$a"))
//						printResponse(result)
						self ! GETALLUSERPOST

			}

			case GETALLUSERPOST =>
			{
			  var a = random()
				val result = pipeline(Get(s"http://localhost:8080/user/post/all?userid=$a"))
//						printResponse(result)

						self ! GETUSERFRIENDS
			}

			case GETUSERFRIENDS =>
			{
			  var a = random()
				val result = pipeline(Get(s"http://localhost:8080/user/friends?userid=$a"))
//						printResponse(result)

						self ! GETALLPAGEPOSTS

			}

			case GETALLPAGEPOSTS =>
			{
			  var a = random()
				val result = pipeline(Get(s"http://localhost:8080/page/post/all?pageid=$pageid"))
//						printResponse(result)

						self !		GETPAGEDETAILS
			}
			case GETPAGEDETAILS =>
			{
			  var a = random()
				val result = pipeline(Get(s"http://localhost:8080/page?pageid=$pageid"))
//						printResponse(result)

			}
	}

	def printResponse(result: scala.concurrent.Future[spray.http.HttpResponse]) {
		result.foreach {
			response =>
			println(s"Request completed with status ${response.status} and content:\n${response.entity.asString}")
		}
	}
}


class InactiveUser(start:Int) extends Actor
{
	implicit val system = context.system
			import system.dispatcher
			var userid = "name"+start
			val pipeline = sendReceive

			def receive = {
			case POSTDETAILS => val post = pipeline(Post(s"http://localhost:8080/new-user?userid=$start&firstname=Harsh&lastname=Kothari&dob=07/08/94&gender=M"))
					//					print(post)
					//     val id = 1
					//     val get_friendlist = pipeline(Get(s"http://localhost:8080/user/friends?userid=$id"))

	}
}

class Page(start:Int) extends Actor
{
	implicit val system = context.system
			import system.dispatcher
			val pipeline = sendReceive

			val r = scala.util.Random
			val a = r.nextInt(100)
			var b = r.nextInt(100)

			if( b==a)
			{
				b = r.nextInt(100)
			}

	val pageid = (Client.x + 1) to r.nextInt(Client.x + 1 + Client.x/10)

			def getRandomPost() : String =
		{
					val filename = "C:/Users/Harsh/Desktop/Facebook.txt" 
							var array : ArrayBuffer[String]  = new ArrayBuffer
							var count : Int=0
							for (line <- Source.fromFile(filename).getLines()) 
							{

								array+=line
										count+=1
							}
					array(Random.nextInt(count))
		}

			def receive = {

			case PAGEPOST => {


				val a = Random.nextInt(Client.x)
						val b = "AmanFanClub"+a
						val post = pipeline(Post(s"http://localhost:8080/addpage?pageid=$start&name=$b&desc=FanClubofapopularperson&startdate=12/12/12"))

			}
			//			case ADDPAGEFOLLOWER => {
			//				var id = Random.nextInt(Client.x)
			//						val post = pipeline(Post(s"http://localhost:8080/page/addfollower?pageid=$start&userid=$id")) 
			//			} 

			case ADDPAGEPOST =>
			{
				var n = Random.nextInt(20)
						if(n == 0)
						{
							n = Random.nextInt(20)
						}			    
				for(i<-0 to n){
					var abc = getRandomPost()
							val post = pipeline(Post(s"http://localhost:8080/page/pagepost?pageid=$start&post=$abc"))
				}
			}

			}
}