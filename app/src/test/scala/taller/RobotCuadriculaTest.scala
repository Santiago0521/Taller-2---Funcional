package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RobotCuadriculaTest extends AnyFunSuite {

  val r = new RobotCuadricula()
  import r._

  val cuadrado = Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha))))

  // Punto 1 -------------------------------------------------------------------

  test("girar: Norte a la derecha da Este") { assert(girar(Norte, Derecha) == Este) }
  test("girar: Norte a la izquierda da Oeste") { assert(girar(Norte, Izquierda) == Oeste) }
  test("girar: Oeste a la derecha da Norte") { assert(girar(Oeste, Derecha) == Norte) }
  test("girar: Sur a la izquierda da Este") { assert(girar(Sur, Izquierda) == Este) }

  test("girar: cuatro giros al mismo lado vuelven al principio") {
    val cuatro = List(Derecha, Derecha, Derecha, Derecha)
      .foldLeft(Norte: Direccion)((d, s) => girar(d, s))
    assert(cuatro == Norte)
  }

  test("girar: un giro y su contrario se cancelan") {
    assert(girar(girar(Este, Derecha), Izquierda) == Este)
  }

  // Punto 2 -------------------------------------------------------------------

  test("ejecutar: avanzar tres al norte") {
    assert(ejecutar(inicial, Avanzar(3)) == Estado(0, 3, Norte))
  }

  test("ejecutar: una secuencia de avance, giro y avance") {
    assert(ejecutar(inicial, Secuencia(List(Avanzar(2), Girar(Derecha), Avanzar(4)))) ==
      Estado(4, 2, Este))
  }

  test("ejecutar: el cuadrado vuelve al punto de partida") {
    assert(ejecutar(inicial, cuadrado) == Estado(0, 0, Norte))
  }

  test("ejecutar: avanzar con paso negativo retrocede") {
    assert(ejecutar(inicial, Avanzar(-2)) == Estado(0, -2, Norte))
  }

  test("ejecutar: repetir cero veces no hace nada") {
    assert(ejecutar(inicial, Repetir(0, Avanzar(5))) == inicial)
  }

  test("ejecutar: la secuencia vacía no hace nada") {
    assert(ejecutar(inicial, Secuencia(Nil)) == inicial)
  }

  test("ejecutar: avanzar al sur disminuye y") {
    assert(ejecutar(Estado(5, 5, Sur), Avanzar(2)) == Estado(5, 3, Sur))
  }

  test("ejecutar: avanzar al oeste disminuye x") {
    assert(ejecutar(Estado(5, 5, Oeste), Avanzar(2)) == Estado(3, 5, Oeste))
  }

  // Punto 3 -------------------------------------------------------------------

  test("cuantasBasicas: una orden básica cuenta uno") {
    assert(cuantasBasicas(Avanzar(3)) == 1)
  }

  test("cuantasBasicas: el cuadrado ejecuta ocho básicas") {
    assert(cuantasBasicas(cuadrado) == 8)
  }

  test("cuantasBasicas: repetir cero veces no ejecuta nada") {
    assert(cuantasBasicas(Repetir(0, cuadrado)) == 0)
  }

  test("cuantasBasicas: la secuencia vacía no ejecuta nada") {
    assert(cuantasBasicas(Secuencia(Nil)) == 0)
  }

  test("profundidad: una orden básica tiene profundidad 1") {
    assert(profundidad(Avanzar(3)) == 1)
  }

  test("profundidad: una secuencia de básicas tiene profundidad 2") {
    assert(profundidad(Secuencia(List(Avanzar(1), Girar(Derecha)))) == 2)
  }

  test("profundidad: el cuadrado tiene profundidad 3") {
    assert(profundidad(cuadrado) == 3)
  }

  test("profundidad: la secuencia vacía tiene profundidad 1") {
    assert(profundidad(Secuencia(Nil)) == 1)
  }

  test("profundidad: manda la rama más honda") {
    assert(profundidad(Secuencia(List(Avanzar(1), cuadrado))) == 4)
  }

  // Punto 4 -------------------------------------------------------------------

  test("componer: deja las dos órdenes en secuencia") {
    assert(componer(Avanzar(1), Girar(Derecha)) ==
      Secuencia(List(Avanzar(1), Girar(Derecha))))
  }

  test("componer: ejecutar la compuesta es ejecutar una y después la otra") {
    assert(ejecutar(inicial, componer(Avanzar(2), Girar(Derecha))) ==
      ejecutar(ejecutar(inicial, Avanzar(2)), Girar(Derecha)))
  }

  test("rutina: currificada, el cuadrado de cuatro vuelve al origen") {
    val cuadradoDe = rutina(1)(Derecha) _
    assert(ejecutar(inicial, cuadradoDe(4)) == Estado(0, 0, Norte))
  }

  test("rutina: con los mismos argumentos construye el cuadrado del enunciado") {
    assert(rutina(1)(Derecha)(4) == cuadrado)
  }

  test("aplicarA: ejecuta la misma orden sobre varios estados") {
    assert(aplicarA(List(inicial, Estado(5, 5, Sur)), Avanzar(2)) ==
      List(Estado(0, 2, Norte), Estado(5, 3, Sur)))
  }

  test("aplicarA: sobre la lista vacía no hay nada que hacer") {
    assert(aplicarA(Nil, Avanzar(2)) == Nil)
  }

  test("transformar: duplicar los avances deja los giros como estaban") {
    val doble = transformar(cuadrado, {
      case Avanzar(n) => Avanzar(2 * n)
      case otra => otra
    })
    assert(doble == Repetir(4, Secuencia(List(Avanzar(2), Girar(Derecha)))))
  }

  test("transformar: con la identidad no cambia nada") {
    assert(transformar(cuadrado, (o: Orden) => o) == cuadrado)
  }

  // Punto 5 -------------------------------------------------------------------

  test("simplificar: dos avances seguidos se juntan") {
    assert(simplificar(Secuencia(List(Avanzar(2), Avanzar(3)))) == Avanzar(5))
  }

  test("simplificar: repetir una vez es la orden misma") {
    assert(simplificar(Repetir(1, Avanzar(4))) == Avanzar(4))
  }

  test("simplificar: avanzar cero desaparece de la secuencia") {
    assert(simplificar(Secuencia(List(Avanzar(0), Girar(Derecha)))) == Girar(Derecha))
  }

  test("simplificar: junta solo los avances contiguos") {
    assert(simplificar(Secuencia(List(Avanzar(1), Avanzar(2), Girar(Derecha), Avanzar(3)))) ==
      Secuencia(List(Avanzar(3), Girar(Derecha), Avanzar(3))))
  }

  test("simplificar: repetir cero veces no hace nada") {
    assert(simplificar(Repetir(0, Avanzar(9))) == Secuencia(Nil))
  }

  test("simplificar: dos avances que se cancelan desaparecen") {
    assert(simplificar(Secuencia(List(Avanzar(2), Avanzar(-2)))) == Secuencia(Nil))
  }

  test("simplificar: es idempotente") {
    val ordenes = List(cuadrado,
      Secuencia(List(Avanzar(1), Avanzar(2), Girar(Derecha), Avanzar(3))),
      Repetir(1, Secuencia(List(Avanzar(0), Avanzar(4)))),
      Secuencia(List(Secuencia(Nil), Repetir(0, Avanzar(3)))))
    assert(ordenes.forall(o => simplificar(simplificar(o)) == simplificar(o)))
  }

  test("simplificar: no cambia el resultado de ejecutar") {
    val estados = List(inicial, Estado(3, -2, Este), Estado(-1, 4, Sur))
    val ordenes = List(cuadrado,
      Secuencia(List(Avanzar(2), Avanzar(3))),
      Secuencia(List(Avanzar(1), Avanzar(2), Girar(Derecha), Avanzar(3))),
      Repetir(1, Avanzar(4)),
      Repetir(0, Avanzar(9)),
      Secuencia(List(Avanzar(0), Girar(Derecha))),
      Secuencia(List(Avanzar(2), Avanzar(-2))),
      Repetir(3, Secuencia(List(Repetir(1, Avanzar(2)), Girar(Izquierda)))))
    assert(ordenes.forall(o =>
      estados.forall(e => ejecutar(e, o) == ejecutar(e, simplificar(o)))))
  }

  test("simplificar: el anidamiento interior también se reduce") {
    assert(simplificar(Repetir(3, Repetir(1, Avanzar(2)))) == Repetir(3, Avanzar(2)))
  }


  // PRUEBAS ADICIONALES: 25 casos nuevos
  // Punto 1: girar ------------------------------------------------------------

  test("girar: Este a la izquierda da Norte") {
    assert(girar(Este, Izquierda) == Norte)
  }

  test("girar: Sur a la derecha da Oeste") {
    assert(girar(Sur, Derecha) == Oeste)
  }

  test("girar: Oeste a la izquierda da Sur") {
    assert(girar(Oeste, Izquierda) == Sur)
  }

  test("girar: cuatro giros a la izquierda conservan la dirección inicial") {
    val cuatro = List(Izquierda, Izquierda, Izquierda, Izquierda)
      .foldLeft(Este: Direccion)((d, s) => girar(d, s))
    assert(cuatro == Este)
  }

  test("girar: dos giros a la derecha producen la dirección opuesta") {
    assert(girar(girar(Norte, Derecha), Derecha) == Sur)
  }

  // Punto 2: ejecutar ---------------------------------------------------------

  test("ejecutar: avanzar negativamente mirando al este") {
    assert(ejecutar(Estado(4, -2, Este), Avanzar(-3)) ==
      Estado(1, -2, Este))
  }

  test("ejecutar: dos repeticiones de una secuencia combinan avance y giro") {
    val orden = Repetir(2, Secuencia(List(Avanzar(2), Girar(Derecha))))
    assert(ejecutar(inicial, orden) == Estado(2, 2, Sur))
  }

  test("ejecutar: girar a la izquierda antes de avanzar") {
    val orden = Secuencia(List(Girar(Izquierda), Avanzar(3)))
    assert(ejecutar(inicial, orden) == Estado(-3, 0, Oeste))
  }

  test("ejecutar: repetir un número negativo no modifica el estado") {
    val estado = Estado(-3, 7, Este)
    assert(ejecutar(estado, Repetir(-2, Avanzar(5))) == estado)
  }

  test("ejecutar: avanzar positivamente mirando al este aumenta x") {
    assert(ejecutar(Estado(-2, 7, Este), Avanzar(5)) ==
      Estado(3, 7, Este))
  }

  // Punto 3: contar órdenes y medir profundidad -------------------------------

  test("cuantasBasicas: cuenta las órdenes de una repetición anidada") {
    val orden = Repetir(3,
      Secuencia(List(Avanzar(2), Repetir(2, Girar(Izquierda)))))
    assert(cuantasBasicas(orden) == 9)
  }

  test("cuantasBasicas: suma las órdenes de secuencias anidadas") {
    val orden = Secuencia(List(
      Avanzar(1),
      Secuencia(List(Girar(Derecha), Repetir(4, Avanzar(1))))
    ))
    assert(cuantasBasicas(orden) == 6)
  }

  test("cuantasBasicas: repetir una cantidad negativa cuenta cero") {
    assert(cuantasBasicas(Repetir(-3,
      Secuencia(List(Avanzar(1), Girar(Derecha))))) == 0)
  }

  test("profundidad: una repetición de una orden básica tiene profundidad dos") {
    assert(profundidad(Repetir(3, Avanzar(2))) == 2)
  }

  test("profundidad: considera varios niveles de anidamiento") {
    val orden = Secuencia(List(
      Repetir(2,
        Secuencia(List(Avanzar(1), Repetir(2, Girar(Derecha))))),
      Girar(Izquierda)
    ))
    assert(profundidad(orden) == 5)
  }

  // Punto 4: composición, rutinas, aplicación y transformación ---------------

  test("componer: conserva el orden de una orden negativa y un giro") {
    assert(componer(Avanzar(-3), Girar(Izquierda)) ==
      Secuencia(List(Avanzar(-3), Girar(Izquierda))))
  }

  test("rutina: construye una repetición con pasos negativos y giro izquierdo") {
    assert(rutina(-2)(Izquierda)(3) ==
      Repetir(3, Secuencia(List(Avanzar(-2), Girar(Izquierda)))))
  }

  test("aplicarA: ejecuta giro izquierdo y avance en estados diferentes") {
    val orden = Secuencia(List(Girar(Izquierda), Avanzar(2)))
    val estados = List(inicial, Estado(2, 3, Este), Estado(-1, 4, Sur))

    assert(aplicarA(estados, orden) == List(
      Estado(-2, 0, Oeste),
      Estado(2, 5, Norte),
      Estado(1, 4, Este)
    ))
  }

  test("transformar: sustituye giros a la derecha por giros a la izquierda") {
    val orden = Repetir(2,
      Secuencia(List(Avanzar(3), Girar(Derecha))))

    val resultado = transformar(orden, {
      case Girar(Derecha) => Girar(Izquierda)
      case otra => otra
    })

    assert(resultado == Repetir(2,
      Secuencia(List(Avanzar(3), Girar(Izquierda)))))
  }

  test("transformar: cambia el signo de los avances anidados") {
    val orden = Secuencia(List(
      Avanzar(2),
      Repetir(3, Secuencia(List(Avanzar(-4), Girar(Derecha))))
    ))

    val resultado = transformar(orden, {
      case Avanzar(n) => Avanzar(-n)
      case otra => otra
    })

    assert(resultado == Secuencia(List(
      Avanzar(-2),
      Repetir(3, Secuencia(List(Avanzar(4), Girar(Derecha))))
    )))
  }

  // Punto 5: simplificar ------------------------------------------------------

  test("simplificar: una secuencia con un solo elemento se reduce al elemento") {
    assert(simplificar(Secuencia(List(Girar(Izquierda)))) == Girar(Izquierda))
  }

  test("simplificar: elimina un avance cero al final de la secuencia") {
    assert(simplificar(Secuencia(List(Girar(Izquierda), Avanzar(0)))) ==
      Girar(Izquierda))
  }

  test("simplificar: una repetición negativa se convierte en una orden vacía") {
    assert(simplificar(Repetir(-2, Avanzar(5))) == Secuencia(Nil))
  }

  test("simplificar: elimina órdenes vacías anidadas y conserva las restantes") {
    val orden = Secuencia(List(
      Secuencia(Nil),
      Avanzar(4),
      Secuencia(List(Avanzar(0))),
      Girar(Izquierda)
    ))

    assert(simplificar(orden) ==
      Secuencia(List(Avanzar(4), Girar(Izquierda))))
  }

  test("simplificar: varios avances contiguos cuya suma es cero desaparecen") {
    val orden = Secuencia(List(Avanzar(7), Avanzar(-3), Avanzar(-4)))
    assert(simplificar(orden) == Secuencia(Nil))
  }

}

