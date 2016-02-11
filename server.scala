package com.example

import spray.http.MediaTypes
import akka.actor._
import akka.io.IO
import spray.routing.SimpleRoutingApp
import akka.actor.ActorSystem
import java.util.HashMap
import scala.collection.immutable.List
import akka.actor.Actor
import scala.concurrent.duration._
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
import org.json4s._
import akka.pattern.ask
import scala.util.Random
import akka.dispatch.OnSuccess

import java.io._
import sun.misc._


case class USERSERVER(userid:String)
case class ADDFRIEND(userid:String,friendid:String)
case class FRIENDLIST(userid:String,friends:List[String])
//case class GETUSERPOST(userid:String,post:String)
//case class GETPAGEPOST(pageid:String,post:String)
case class GETUSERPOSTLIST(userid:String,posts:List[String])
case class GETPAGEPOSTLIST(pageid:String,posts:List[String])
case class PROFILE(userid:String,posts:List[String])
case class USERDETAILS(userid:String,posts:List[String])
case class PAGEDETAILS(pageid:String,posts:List[String])

object JsonProtocol extends DefaultJsonProtocol {
	implicit val userformat = jsonFormat1(USERSERVER)
			implicit val addfriendformat = jsonFormat2(ADDFRIEND)
			implicit val friendlistformat = jsonFormat2(FRIENDLIST)
			//  implicit val userpostformat = jsonFormat2(GETUSERPOST)
			implicit val userpostlistformat = jsonFormat2(GETUSERPOSTLIST)
			//  implicit val pagepostformat = jsonFormat2(GETPAGEPOST)
			implicit val pagepostlistformat = jsonFormat2(GETPAGEPOSTLIST)
			implicit val profileformat = jsonFormat2(PROFILE)
			implicit val userdetailsformat = jsonFormat2(USERDETAILS)
			implicit val pagedetailsformat = jsonFormat2(PAGEDETAILS)
}

object Server extends App with SimpleRoutingApp{

	import JsonProtocol._
	import spray.httpx.SprayJsonSupport._

	var noOfUsers = 0
	var users:ArrayBuffer[String] = new ArrayBuffer[String]
			var pages:ArrayBuffer[String] = new ArrayBuffer[String]
					var infoMap = new HashMap[String,Details]        
							//var posts = new ArrayBuffer[String]
							implicit val system = ActorSystem("Fb-server")
							implicit val timeout = Timeout(3.seconds)
							import system.dispatcher
							//var friends:Set[String] = Set()
							var friendMap = new HashMap[String, Set[String]]
							var postMap = new HashMap[String, ArrayBuffer[String]]
							var pageMap = new HashMap[String, Page_Details ]
							var pageFollowerMap = new HashMap[String,Set[String]]
							var pagePostMap = new HashMap[String,ArrayBuffer[String]]
							var userPagesMap = new HashMap[String,Set[String]]
							var userPhotoMap = new HashMap[String,ArrayBuffer[String]]
							var userAlbumMap = new HashMap[String,String]
							var albumPhotoMap = new HashMap[String,ArrayBuffer[String]]
							
							val addFriend = system.actorOf(Props(new AddFriend()))
							val addUser = system.actorOf(Props(new AddUser()))
							val addPost = system.actorOf(Props(new AddPost()))
							val addPage = system.actorOf(Props(new AddPage()))
							val addFollower = system.actorOf(Props(new AddFollower()))
							val pagePost = system.actorOf(Props(new PagePost()))
							val userPhoto = system.actorOf(Props(new UserPhoto()))
							val userAlbum = system.actorOf(Props(new UserAlbum()))

							val getFriendList = system.actorOf(Props(new GetFriendList()))
							val getUserPostList = system.actorOf(Props(new GetUserPostList()))
							val getPagePostList = system.actorOf(Props(new GetPagePostList()))
							val getProfile = system.actorOf(Props(new GetProfile()))
							val getUserDetails = system.actorOf(Props(new UserDetails()))
							val getPageDetails = system.actorOf(Props(new PageDetails()))
							val getPage = system.actorOf(Props(new GetPage()))
							//  val getUserPost = system.actorOf(Props(new GetUserPost()))
							//  val getPagePost = system.actorOf(Props(new GetPagePost()))

																					

																					startServer(interface="localhost",port=8080)
																					{
		get {
			path(""){ ctx =>
			ctx.complete("Facebook demo")
			}
		}~
		get{
			path("user" / "friends"){
				respondWithMediaType(MediaTypes.`application/json`) {
					parameter("userid"){(userid) =>
					complete{
						(getFriendList ? userid).mapTo[String].map(s => s"$s")
					}
					}
				}
			}
		}~
		//		get{
		//			path("user" / "post"){
		//			  respondWithMediaType(MediaTypes.`application/json`) {
		//				parameter("userid","postid"){(userid,postid) =>
		//				complete{
		//				(getUserPost ? (userid,postid)).mapTo[String].map(s => s"$s")
		//				}
		//				}
		//				}
		//			}
		//		}~
		get{
			path("user" / "post" / "all"){
				respondWithMediaType(MediaTypes.`application/json`) {
					parameter("userid"){(userid) =>
					complete{
						(getUserPostList ? userid).mapTo[String].map(s => s"$s")
					}
					}
				}
			}
		}~
		//		get{
		//			path("page" / "post"){
		//			  respondWithMediaType(MediaTypes.`application/json`) {
		//				parameter("pageid","postid"){(pageid,postid) =>
		//				complete{
		//				(getPagePost ? (pageid,postid)).mapTo[String].map(s => s"$s")
		//				}
		//				}
		//				}
		//			}
		//		}~
		get{
			path("page" / "post" / "all"){
				respondWithMediaType(MediaTypes.`application/json`) {
					parameter("pageid"){(pageid) =>
					complete{
						(getPagePostList ? pageid).mapTo[String].map(s => s"$s")
					}
					}
				}
			}
		}~
		get{
			path("userdetails"){
				respondWithMediaType(MediaTypes.`application/json`) {
					parameter("userid"){(userid) =>
					complete{
						(getUserDetails ? userid).mapTo[String].map(s => s"$s")
					}
					}
				}
			}
		}~
		get{
			path("userprofile"){
				respondWithMediaType(MediaTypes.`application/json`) {
					parameter("userid"){(userid) =>
					complete{
						(getProfile ? userid).mapTo[String].map(s => s"$s")
					}
					}
				}
			}
		}~
		get{
			path("page"){
				respondWithMediaType(MediaTypes.`application/json`) {
					parameter("pageid"){(pageid) =>
					complete{
						(getPageDetails ? pageid).mapTo[String].map(s => s"$s")
					}
					}
				}
			}
		}~
		get{
			path("pageinfo"){
				respondWithMediaType(MediaTypes.`application/json`) {
					parameter("pageid"){(pageid) =>
					complete{
						(getPage ? pageid).mapTo[String].map(s => s"$s")
					}
					}
				}
			}
		}~
		post{
			path("new-user"){
				parameter("userid","firstname","lastname","dob","gender") { (userid,firstname,lastname,dob,gender) =>
				addUser ! (userid,firstname,lastname,dob,gender)
				complete{
					"USER CREATED"
				}
				}
			}
		}~
		post{
			path("user"/"addfriend"){
				parameter("userid","friendid"){(userid,friendid) =>
				addFriend ! (userid,friendid)
				complete{
					"Friend Added"
				}
				}
			}
		}~
		post{
			path("user"/"addpost"){
				parameter("userid","post"){(userid,post) =>
				addPost ! (userid,post)
				complete{
					"Post Added"
				}
				}
			}
		}~
		post{
			path("addpage"){
				parameter("pageid","name","desc","startdate"){(pageid,name,desc,startdate) =>
				addPage ! (pageid,name,desc,startdate)
				complete{
					"Page Added"
				}
				}
			}
		}~
		post{
			path("page"/"addfollower"){
				parameter("pageid","userid"){(pageid,userid) =>
				addFollower ! (pageid,userid)
				complete{
					"Follower Added"
				}
				}
			}
		}~
		post{
			path("page"/"pagepost"){
				parameter("pageid","post"){(pageid,post) =>
				pagePost ! (pageid,post)
				complete{
					"post Added"
				}
				}
			}
		}~
		post{
			path("user"/"uploadphoto"){
				parameter("userid","pic"){(userid,pic) =>
				userPhoto ! (userid,pic)
				complete {
					"Photo uploaded"
				}
				}
			}
		}~		
		post{
			path("user"/"uploadalbum"){
				parameter("userid","albumid","pic"){(userid,albumid,pic) =>
				userAlbum ! (userid,albumid,pic)
				complete {
					"album created"
				}
				}
			}
		}
}

	class AddFriend extends Actor{
		def receive = {

		case(userid : String , friendid : String) =>

		if(friendid != userid){
			if(friendMap.get(userid) == null ){

				if(friendMap.get(friendid) == null){
					var set:Set[String] = Set()
							var set1:Set[String] = Set()
							set += friendid
							set1 += userid
							friendMap.put(friendid, set1)
							friendMap.put(userid, set)
				}
				else{
					var set:Set[String] = Set()
							var set1 = friendMap.get(friendid)
							set += friendid
							set1 += userid
							friendMap.put(friendid, set1)
							friendMap.put(userid, set)
				}
			}
			else
			{
				if(friendMap.get(friendid) == null){
					var set = friendMap.get(userid)
							var set1:Set[String] = Set()
							set += friendid
							set1 += userid
							friendMap.put(friendid, set1)
							friendMap.put(userid, set)

				}
				else{
					var set = friendMap.get(userid)
							var set1 = friendMap.get(friendid)
							set += friendid
							set1 += userid
							friendMap.put(friendid, set1)
							friendMap.put(userid, set)
				}
			}
		}
		}
	}

	class AddUser extends Actor{
		def receive = {

		case(userid : String, firstname : String, lastname:String, dob : String, gender : String) =>
		if(infoMap.get(userid) == null){
			users += userid
					var det = new Details()
			det.userid = userid
			det.dob = dob
			det.gender = gender
			det.firstname = firstname
			det.lastname = lastname
			infoMap.put(userid, det)  
		}
		}
	}

	class AddPage extends Actor{
		def receive = {
		case (pageid : String,name : String, desc : String,startdate : String) =>
		pages += pageid

		if(pageMap.get(pageid) == null){
			var det = new Page_Details()
			det.desc = desc
			det.name = name
			det.startdate = startdate
			pageMap.put(pageid, det)
		}
		else{

			var det =  pageMap.get(pageid)
					det.desc = desc
					det.name = name
					det.startdate = startdate
					pageMap.put(pageid, det)        

		}
		}
	}

	class AddFollower extends Actor{

		def receive = {  
		case (pageid : String, userid : String) =>
		var pg_name = ""

		if(pageMap.get(pageid) != null){
			var pg = pageMap.get(pageid)
					pg_name = pg.name 

					if(pageFollowerMap.get(pageid) == null){
						var set : Set[String] = Set()

								if(infoMap.get(userid) != null){
									var det = infoMap.get(userid)
											var str = ""
											str += det.firstname
											str += " "
											str += det.lastname
											set += str
											pageFollowerMap.put(pageid, set)

											if(userPagesMap.get(userid) != null){
												var set1 = userPagesMap.get(userid)
														set1 += pg_name
														userPagesMap.put(userid,set1)
											}
											else{
												var set1 : Set[String] = Set()
														set1 += pg_name
														userPagesMap.put(userid,set1)
											}
								}			
					}

					else{
						var set = pageFollowerMap.get(pageid)
								if(infoMap.get(userid) != null){
									var det = infoMap.get(userid)
											var str = ""
											str += det.firstname
											str += " "
											str += det.lastname
											set += str
											pageFollowerMap.put(pageid, set)
								}
					}
		}
		else{

		}
		}
	}


	class AddPost extends Actor{
		def receive = {

		case(userid : String, post : String) =>

		  if(post != null){
		if(postMap.get(userid) == null){
			var arr:ArrayBuffer[String] = new ArrayBuffer[String]
					arr += post
					postMap.put(userid, arr)
		}
		else{
			var arr = postMap.get(userid)
					arr += post
					postMap.put(userid, arr)
		}
		}
	}
	}
	class UserPhoto extends Actor{
		def receive = { 

		case(userid:String,pic:String) =>
		  var l = Random.nextInt(10000)
		  val path = new File(s"""C:/Users/Harsh/Desktop/images/image$l.jpg""")
		if(infoMap.get(userid) != null)
		{
			var p = new BASE64Decoder().decodeBuffer(pic)
			val stream = new FileOutputStream(path)
			stream.write(p)
			stream.close()
					var p1 = p.toString()
					if(userPhotoMap.get(userid) == null)
					{
						var temp : ArrayBuffer[String] = new ArrayBuffer[String] 
								temp += p1
								userPhotoMap.put(userid,temp)
					}
					else{
						var temp = userPhotoMap.get(userid)
								temp += p1
								userPhotoMap.put(userid,temp)
					}
		}
		}
	}
	
	class UserAlbum extends Actor{
	  def receive = {
	  case (userid:String,albumid:String,pic:String) =>
		  var l = Random.nextInt(10000)
		  val path = new File(s"""C:/Users/Harsh/Desktop/images1/image$l.jpg""")
		if(infoMap.get(userid) != null)
		{
			var p = new BASE64Decoder().decodeBuffer(pic)
			val stream = new FileOutputStream(path)
			stream.write(p)
			stream.close()
					var p1 = p.toString()
					if(userAlbumMap.get(userid) == null)
					{
						var temp : ArrayBuffer[String] = new ArrayBuffer[String] 
								temp += p1
								albumPhotoMap.put(albumid, temp)
								userAlbumMap.put(userid,albumid)
					}
					else{
						var temp = albumPhotoMap.get(albumid)
								temp += p1
								albumPhotoMap.put(albumid,temp)
					}
		}
		}
	}

	class PagePost extends Actor{
		def receive = {
		case (pageid:String,post:String) =>
		if(pagePostMap.get(pageid) == null){
			var arr:ArrayBuffer[String] = new ArrayBuffer[String]
					arr += post
					pagePostMap.put(pageid, arr)
		}
		else
		{
			var arr = pagePostMap.get(pageid)
					arr += post
					pagePostMap.put(pageid, arr)          
		}
		}
	}

	class GetFriendList extends Actor{
		def receive = {

		case (userid:String) =>

		if ((friendMap.get(userid)) != null)
		{
			var arr = friendMap.get(userid)
					var list : ArrayBuffer[String] = new ArrayBuffer[String]

					list += "Friend List:"
							for(users <- arr){
								if(infoMap.get(users) != null){
									var str : String = ""
											var arr1 = new Details()
								arr1 = infoMap.get(users)

								
								str += arr1.firstname
								str += " "
								str += arr1.lastname
								str += " "

								list += str
								}
							}

			var arr2 = list.toList
					sender ! (arr2.toJson.prettyPrint)          
		}   
		}
	}

	class GetUserPostList extends Actor{
		def receive = {
		case (userid : String) =>
		if(postMap.get(userid) != null)
		{
			var array = postMap.get(userid)
					var arr = array.toList
					sender ! (arr.toJson.prettyPrint)          

		}
		else
		{
			var arr = "NO POST FOR THIS USER EXISTS"
					sender ! (arr.toJson.prettyPrint)
		}
		}
	}

	//	class GetUserPost extends Actor{
	//	  def receive = {
	//	  case (userid : String, postid : String) =>
	//	    
	//	}
	//	}

	class GetPagePostList extends Actor{
		def receive = {
		case (pageid : String) =>
		if(pagePostMap.get(pageid) != null)
		{
			var array = pagePostMap.get(pageid)
					var arr = array.toList
					sender ! (arr.toJson.prettyPrint)          

		}
		else
		{
			var arr = "NO POSTS FOR THIS PAGE EXISTS"
					sender ! (arr.toJson.prettyPrint)
		}

		}	    
	}

	//	class GetPagePost extends Actor{
	//	  def receive = {
	//	  case (pageid : String, postid : String) =>
	//	    
	//	}
	//	}

	class GetProfile extends Actor{
		def receive = {

		case (userid : String) =>

		if(infoMap.get(userid) != null)
		{
			var post_arr : List[String] = List()
					var friend_arr : List[String] = List()

					var details = infoMap.get(userid)

					var dets_arr : ArrayBuffer[String] = new ArrayBuffer[String]
					    dets_arr += "User Details"
							dets_arr += "First name: "+details.firstname
							dets_arr += "Last name: "+details.lastname
							dets_arr += "Date Of Birth: "+details.dob
							dets_arr += "Gender: "+details.gender
							dets_arr += "------"
							dets_arr += "FriendList"
							//			var details_arr = dets_arr.toList


							if ((friendMap.get(userid)) != null)
							{
								var arr = friendMap.get(userid)
										var list : ArrayBuffer[String] = new ArrayBuffer[String]

												for(users <- arr){
													if(infoMap.get(users) != null){
														var str : String = ""
																var arr1 = new Details()
													arr1 = infoMap.get(users)

													str += arr1.firstname
													str += " "
													str += arr1.lastname
													str += " "

													dets_arr += str
													}
												}

								//				friend_arr = list.toList
								//					sender ! (arr2.toJson.prettyPrint) 

								dets_arr += "------"	
								dets_arr += "Posts:"
							}

					if(postMap.get(userid) != null)
					{
						var array = postMap.get(userid)
								//						post_arr = array.toList

								for(arr <- array){
									dets_arr += arr
								}
						dets_arr += "-----"
						dets_arr += "Pages:"
								//     	    sender ! (arr.toJson.prettyPrint)          
					}
					else
					{
						//                				var post_arr = "NO SUCH USER EXISTS"
						//	      sender ! (arr.toJson.prettyPrint)
					}

					if(userPagesMap.get(userid) != null){
						var pg = userPagesMap.get(userid)
								var pg1 = pg.toList

								for(follow <- pg1)
								{
									dets_arr += follow 
								}				 
					}

					sender ! (dets_arr.toList.toJson.prettyPrint)

		}

		//			if(post_arr.isEmpty)
		//			{
		//      var str:ArrayBuffer[String] = new ArrayBuffer[String]
		//      str += "No posts for this user"
		//      post_arr = str.toList
		//			}
		//			if(friend_arr.isEmpty)
		//			{
		//			  var str:ArrayBuffer[String] = new ArrayBuffer[String]
		//			   str += "No friends to display"
		//			  friend_arr = str.toList
		//			}

		//			var information : List[String] = List()
		//			information = details_arr + friend_arr + post_arr

		else{
			var list = "No such user exists"
					sender ! (list.toJson.prettyPrint)
		}
		}
	}

	class PageDetails extends Actor{
		def receive = {
		case (pageid : String) =>
		var page_dets : ArrayBuffer[String] = new ArrayBuffer[String]

				if(pageMap.get(pageid) != null)
				{
					var pg = pageMap.get(pageid)
					    page_dets += "Page Info:"
					    page_dets += pg.name
							page_dets += pg.desc
							page_dets += pg.startdate
							page_dets += "-----"
							page_dets += "Followers:"
							
							if(pageFollowerMap.get(pageid) != null)
							{
								var pg1 = pageFollowerMap.get(pageid)
										var pg2 = pg1.toList

										for(follow <- pg2)
										{
											page_dets += follow 
										}	
								page_dets += "-----"
								
								page_dets += "Posts:"

										if(pagePostMap.get(pageid) != null)
										{
											var pg3 = pagePostMap.get(pageid)
													var pg4 = pg3.toList

													for(follow1 <- pg4)
													{
														page_dets += follow1 
													}

										}
							}
					sender ! (page_dets.toList.toJson.prettyPrint)
				}
				else{
					var list = "No such page exists"
							sender ! (list.toJson.prettyPrint)
				}


		}
	}

	class UserDetails extends Actor{
		def receive = {
		case (userid : String) =>

		if(infoMap.get(userid) != null){

			var list : ArrayBuffer[String] = new ArrayBuffer[String]
					var str : String = ""
					var arr1 = new Details()
			arr1 = infoMap.get(userid)

			str += arr1.firstname
			str += " "
			str += arr1.lastname
			str += " "
			list += str

			var arr2 = list.toList
			sender ! (arr2.toJson.prettyPrint)
		}
		else{
			var arr = "USER NOT FOUND"
					sender ! (arr.toList.toJson.prettyPrint)
		}
		}
	}

	class GetPage extends Actor{
		def receive = {
		case (pageid : String) =>

		if(pageMap.get(pageid) != null){

			var list : ArrayBuffer[String] = new ArrayBuffer[String]
					var str : String = ""
					var arr1 = new Page_Details()
			arr1 = pageMap.get(pageid)

			str += arr1.name
			str += " "
			str += arr1.desc
			str += " "
			list += str

			var arr2 = list.toList
			sender ! (arr2.toJson.prettyPrint)
		}
		else{
			var arr:ArrayBuffer[String] = new ArrayBuffer[String]
					arr += "Page NOT FOUND"
					sender ! (arr.toList.toJson.prettyPrint)
		}
		}
	}

	class Details{
		var userid:String = ""
				var firstname:String = ""
				var lastname:String = ""
				var dob:String = ""
				var gender:String = ""
	}

	class Page_Details{
		var name:String = ""
				var desc:String = ""
				var startdate:String = ""
	}
}
