package chapter01

sealed trait Cause[+E]

object Cause:
  final case class Die(t: Throwable) extends Cause[Nothing]

  final case class Fail[+E](e: E) extends Cause[E]

sealed trait Exit[+E, +A]

object Exit:
  final case class Success[+A](value: A) extends Exit[Nothing, A]

  final case class Failure[+E](cause: Cause[E]) extends Exit[E, Nothing]

//For a mental model zio effect as a function R => Either[E, A]
final case class ZIO[-R, +E, +A](run: R => Either[E, A]):
  self =>
  def map[B](f: A => B): ZIO[R, E, B] = ZIO(r => self.run(r).map(f))

  def flatMap[R1 <: R, E1 >: E, B](f: A => ZIO[R1, E1, B]): ZIO[R1, E1, B] =
    ZIO(r => self.run(r).fold(ZIO.fail(_), f).run(r))

  //Error handling providing the failure if original effect fail it can recovered by provided failure
  def foldZIO[R1 <: R, E1, B](failure: E => ZIO[R1, E1, B], success: A => ZIO[R1, E1, B]): ZIO[R1, E1, B] =
    ZIO(r => self.run(r).fold(failure, success).run(r))

  // effect that can not fail
  def fold[B](failure: E => B, success: A => B): ZIO[R, Nothing, B] =
    ZIO(r => Right(self.run(r).fold(failure, success)))

  // return an effect which does not required any environment
  def provide(r: R): ZIO[Any, E, A] = ZIO(_ => self.run(r))

  def zipWith[R1 <: R, E1 >: E, B, C](that: ZIO[R1, E1, B])(f: (A, B) => C): ZIO[R1, E1, C] = self.flatMap(a => that.map(b => f(a, b)))

  def orElse[R1 <: R, E1 >: E, A1 >: A](that: ZIO[R1, E1, A1]): ZIO[R1, E1, A1] = ZIO(r => self.run(r) match {
    case Left(_) => that.run(r)
    case Right(value) => Right(value)
  })

/*
  def foldCauseZIO[R1 <: R, E1, B](failure: Cause[E] => ZIO[R1, E1, B],
                                    success: A => ZIO[R1, E1, B]
                                  ): ZIO[R1, E1, B] =
    ZIO(r => self.run(r).fold(failure, success).run(r))
*/


object ZIO:

  // Effect that does not required any env, may fail with E, or succeed with A
  type IO[+E, +A] = ZIO[Any, E, A]

  //Effect that does not reqquired any env, may fail with Throwable, or succeed with A
  type Task[+A] = ZIO[Any, Throwable, A]

  //Effect that requires an environment of type R, may fail with Throwable, or succeed with A
  type RIO[-R, +A] = ZIO[R, Throwable, A]

  //Effect that does not required any env, can not fail, and succeed with A
  type UIO[+A] = ZIO[Any, Nothing, A]

  //Effect that requires an environment of type R,can not fail and may succed with A
  type URIO[-R, +A] = ZIO[R, Nothing, A]

  def fail[E](e: => E): ZIO[Any, E, Nothing] = ZIO(_ => Left(e))

  def succeed[A](a: => A): ZIO[Any, Nothing, A] = ZIO(_ => Right(a))

  def attempt[A](a: => A): ZIO[Any, Throwable, A] = ZIO(_ => try Right(a) catch case t: Throwable => Left(t))

  //creates a new effect with a required env as a success value which can be mapped or flatMap
  def environment[R]: ZIO[R, Nothing, R] = ZIO(r => Right(r))

  def collectAll[R, E, A](in: Iterable[ZIO[R, E, A]]): ZIO[R, E, List[A]] = {
    if in.isEmpty then ZIO.succeed(List.empty)
    else in.head.zipWith(collectAll(in.tail))(_ :: _)
  }

  def foreach[R, E, A, B](in: Iterable[A])(f: A => ZIO[R, E, B]): ZIO[R, E, List[B]] = collectAll(in.map(f))




