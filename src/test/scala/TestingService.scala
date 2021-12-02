import zio._
import zio.Console._
import zio.test._
import zio.test.Assertion._
import scala.concurrent.duration._
import scala.language.postfixOps
import zio.test.TestAspect._

object TestingService extends DefaultRunnableSpec :
  val greet: ZIO[Has[Console], Nothing, Unit] =
    for {
      name <- readLine.orDie
      _ <- printLine(s"Hello, $name!").orDie
    } yield ()

  val goShopping: ZIO[Has[Console] with Has[Clock], Nothing, Unit] = printLine("Going Shopping!").orDie.delay(1.hour)

  override def spec: ZSpec[TestEnvironment, Any] = suite("ExampleSpec") (
    test("greet says hello to the user") {
      for {
        _ <- TestConsole.feedLines("Jane")
        _ <- greet
        value <- TestConsole.output
      } yield assert(value)(equalTo(Vector("Hello, Jane!\n")))
    },
    test("goShopping delays for one hour") {
      for {
        fiber <- goShopping.fork
        _ <- TestClock.adjust(1.hour)
        _ <- fiber.join
      } yield assertCompletes
    },
    test("this test will be reapated to ensure it is stable"){
      assertM(ZIO.succeed(1+1))(equalTo(2))
    } @@ nonFlaky
  )


