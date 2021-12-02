package chapter08

import zio.*

object ZScopeInAction:

  val runtime = Runtime.default

  val program1: UIO[Boolean] =
    for
      ref <- Ref.make(false)
      fiber <- ZIO.scopeWith(scope => scope.ensure(_ => ref.set(true)))
        .fork
      _ <- fiber.await
      value <- ref.get
    yield value

  val program2: UIO[Boolean] =
    for
      ref <- Ref.make(false)
      promise <- Promise.make[Nothing, Unit]
      open <- ZScope.make[Exit[Any, Any]]
      effect = (promise.succeed(()) *> ZIO.never).ensuring(ref.set(true))
      _ <- effect.forkIn(open.scope)
      _ <- promise.await
      _ <- open.close(Exit.unit)
      value <- ref.get
    yield value

  @main def runZscopeInAction1: Unit = println(runtime.unsafeRun(program1))

  @main def runZscopeInAction2: Unit = println(runtime.unsafeRun(program2))