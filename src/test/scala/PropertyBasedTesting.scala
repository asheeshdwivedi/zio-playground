import zio._
import zio.test._
import zio.test.Assertion._
import zio.Random._

object PropertyBasedTesting extends DefaultRunnableSpec :

  val intGen: Gen[Has[Random], Int] = Gen.int

  final case class User(name: String, age: Int)

  val genName: Gen[Has[Random] with Has[Sized], String] = Gen.asciiString

  val genAge: Gen[Has[Random], Int] = Gen.int(18, 120)

  val genUser: Gen[Has[Random] with Has[Sized], User] = for {
    name <- genName
    age <- genAge
  } yield User(name = name, age = age)

  override def spec: ZSpec[TestEnvironment, Any] = suite("PropertyBasedTesting")(
    test("integer addition is associative") {
      check(intGen, intGen, intGen) { (x, y, z) =>
        val left = (x + y) + z
        val right = x + (y + z)
        assert(left)(equalTo(right))
      }
    }
  )


