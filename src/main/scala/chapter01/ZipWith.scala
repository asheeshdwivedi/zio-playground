package chapter01

import chapter01.ZipWith.{fullName, printLine}
import zio.{ZIO, ZIOAppDefault}

import scala.io.StdIn

object ZipWith extends ZIOAppDefault :

  val firstName = ZIO.attempt(StdIn.readLine("What is your first name "))
  val lastName = ZIO.attempt(StdIn.readLine("What is your last name "))

  // zip, which sequentially combines the results of two effects into a tuple of their results
  val fullName = firstName.zipWith(lastName)((firstName, lastName) => s"$firstName $lastName")
  // zipLeft/<*  which sequentially combines two effect returing the result of left
  val hello = ZIO.attempt(println("Hello, ")) <* ZIO.attempt(println("World!\n"))

  //zipRight/ *>, which sequentially combines two effects returning the result of the second
  val world = ZIO.attempt(print("Hello, ")) *> ZIO.attempt(print("World!\n"))



  def printLine(line: String) = ZIO.attempt(println(line))

  val result = for {
    line <- fullName
    _ <- printLine(line)
  } yield ()

  override def run: ZIO[Any, Any, Any] = world.exitCode




