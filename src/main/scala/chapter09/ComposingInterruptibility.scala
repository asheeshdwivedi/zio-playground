package chapter09

import zio._

/**
 * def uninterruptibleMask[R, E, A](k: ZIO.InterruptStatusRestore => ZIO[R, E, A]) : ZIO[R,E,A]
 */
object ComposingInterruptibility:
  def myOperator[R, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A] =
    ZIO.uninterruptibleMask { restore =>
      UIO(println("Some work that shouldn't be interrupted")) *>
        restore(zio) <*
        ZIO.succeed(println("SOme other work that shouldn't be interrupted"))
    }

