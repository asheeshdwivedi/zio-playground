import zio.test._
import zio.test.Assertion._
import zio._

object ExampleSpec extends DefaultRunnableSpec :

  override def spec: ZSpec[TestEnvironment, Any] = suite("ExampleSPec")(
    test("addition work") {
      assert(1 + 1)(equalTo(2))
    },
    test("ZIO.succeed succeeds with specified value") {
      assertM(ZIO.succeed(1 + 1))(equalTo(2))
    },
    test("testing an effect using map operator") {
      ZIO.succeed(1 + 1).map(n => assert(n)(equalTo(2)))
    },
    test("testing an effect using a for comprehension") {
      for {
        n <- ZIO.succeed(1 + 1)
      } yield assert(n)(equalTo(2))
    },
    test("And"){
      for {
        x <- ZIO.succeed(1)
        y <- ZIO.succeed(2)
      } yield assert(x)(equalTo(1)) && assert(y)(equalTo(2))
    },
    test("hasSameElement"){
      assert(List(1,2,3))(hasSameElements(List(3,1,2)))
    },
    test("fails"){
      for {
        exit <- ZIO.attempt(1/0).catchAll(_ => ZIO.fail(())).exit
      } yield assert(exit)(fails(isUnit))
    },

/*
    test("collection has only one value and all non negative integer"){
      val assertion:Assertion[Iterable[Int]] =
      assert(List(1, 2, 3))(isNonEmpty && forall(nonNegative))
    }
*/

  )


