# Informe de corrección

**Asignatura:** Fundamentos de Programación Funcional y Concurrente  
**Taller 2:** Un robot en la cuadrícula

## 1. Introducción

En este documento se argumenta la corrección de las funciones `girar` y `ejecutar`, correspondientes a los puntos 1 y 2 del taller.

Para justificar la corrección se analiza el comportamiento de cada función con respecto a su especificación.

En la función `girar` se utiliza un análisis de casos. Para la función `ejecutar` se analiza cada alternativa del tipo recursivo `Orden` y se utiliza inducción estructural en los casos recursivos.

## 2. Corrección de la función `girar`

La función tiene la siguiente especificación:

```scala
def girar(d: Direccion, s: Sentido): Direccion
```

Su objetivo es devolver la dirección resultante de girar 90 grados desde la dirección `d`, siguiendo el sentido `s`.

### 2.1. Especificación matemática

Las direcciones posibles son:

$$
D = \{Norte, Este, Sur, Oeste\}
$$

Los sentidos posibles son:

$$
S = \{Izquierda, Derecha\}
$$

Por lo tanto, existen ocho combinaciones posibles entre una dirección y un sentido.

La especificación de la función es:

| Dirección | Derecha | Izquierda |
|---|---|---|
| Norte | Este | Oeste |
| Este | Sur | Norte |
| Sur | Oeste | Este |
| Oeste | Norte | Sur |

### 2.2. Demostración por análisis de casos

La implementación utiliza `match` sobre el par `(d, s)` y contempla las ocho combinaciones posibles.

Para el sentido `Derecha`:

- Si `d = Norte`, el resultado es `Este`.
- Si `d = Este`, el resultado es `Sur`.
- Si `d = Sur`, el resultado es `Oeste`.
- Si `d = Oeste`, el resultado es `Norte`.

Estos resultados corresponden a un giro de 90 grados hacia la derecha.

Para el sentido `Izquierda`:

- Si `d = Norte`, el resultado es `Oeste`.
- Si `d = Oeste`, el resultado es `Sur`.
- Si `d = Sur`, el resultado es `Este`.
- Si `d = Este`, el resultado es `Norte`.

Estos resultados corresponden a un giro de 90 grados hacia la izquierda.

Las ocho combinaciones posibles están contempladas en la función. Por lo tanto, para cualquier dirección y sentido válidos, la función devuelve la dirección correspondiente.

En consecuencia, `girar` es correcta con respecto a su especificación.

## 3. Corrección de la función `ejecutar`

La función tiene la siguiente especificación:

```scala
def ejecutar(e: Estado, o: Orden): Estado
```

Su objetivo es devolver el estado que resulta de aplicar la orden `o` al estado inicial `e`.

Un estado puede representarse como:

$$
e = (x,y,d)
$$

donde `x` e `y` representan las coordenadas del robot y `d` representa su dirección.

La demostración se realiza analizando cada alternativa del tipo `Orden`.

## 3.1. Caso `Avanzar(n)`

La especificación establece que el robot debe desplazarse `n` unidades en la dirección hacia la que está orientado.

Si:

$$
e=(x,y,Norte)
$$

entonces:

$$
ejecutar(e,Avanzar(n))=(x,y+n,Norte)
$$

Si:

$$
e=(x,y,Este)
$$

entonces:

$$
ejecutar(e,Avanzar(n))=(x+n,y,Este)
$$

Si:

$$
e=(x,y,Sur)
$$

entonces:

$$
ejecutar(e,Avanzar(n))=(x,y-n,Sur)
$$

Si:

$$
e=(x,y,Oeste)
$$

entonces:

$$
ejecutar(e,Avanzar(n))=(x-n,y,Oeste)
$$

En todos los casos se modifica únicamente la coordenada correspondiente al desplazamiento y se conserva la dirección.

Además, cuando `n` es negativo, las mismas expresiones producen un desplazamiento en sentido contrario.

Por lo tanto, el caso `Avanzar(n)` es correcto.

## 3.2. Caso `Girar(sentido)`

La orden `Girar(sentido)` debe cambiar la orientación del robot sin modificar su posición.

La implementación produce:

$$
(x,y,girar(d,s))
$$

donde `d` es la dirección actual y `s` es el sentido del giro.

Como se demostró anteriormente que `girar` es correcta, la nueva dirección corresponde al giro solicitado.

Las coordenadas `x` e `y` permanecen iguales.

Por lo tanto, el caso `Girar(sentido)` es correcto.

## 3.3. Caso `Repetir(veces, orden)`

La orden `Repetir` ejecuta una orden varias veces.

### Caso base

Si:

$$
veces \leq 0
$$

la función devuelve el estado original:

$$
ejecutar(e,Repetir(veces,orden))=e
$$

Esto es correcto porque una cantidad de repeticiones igual a cero o negativa no debe producir ningún movimiento.

### Caso recursivo

Si:

$$
veces > 0
$$

la función ejecuta primero la orden una vez:

$$
e' = ejecutar(e,orden)
$$

y después ejecuta la misma orden con una repetición menos:

$$
ejecutar(e',Repetir(veces-1,orden))
$$

Para demostrar la corrección utilizamos inducción sobre `veces`.

**Caso base:** para `veces <= 0`, no se ejecuta la orden y se devuelve `e`, que coincide con la especificación.

**Hipótesis de inducción:** suponemos que ejecutar una orden `k` veces produce el estado correcto.

**Paso inductivo:** para `k + 1` repeticiones, primero se ejecuta la orden una vez, obteniendo el estado correcto después de una ejecución. Después se ejecuta recursivamente la misma orden `k` veces. Por la hipótesis de inducción, las `k` ejecuciones restantes producen el estado correcto.

Por lo tanto, la ejecución de `k + 1` repeticiones también es correcta.

Además, en cada llamada recursiva el número de repeticiones disminuye en uno, por lo que finalmente se alcanza el caso base.

## 3.4. Caso `Secuencia(Nil)`

Una secuencia vacía no contiene órdenes que ejecutar.

La implementación devuelve directamente el estado recibido:

$$
ejecutar(e,Secuencia(Nil))=e
$$

Esto coincide con la especificación.

Por lo tanto, el caso `Secuencia(Nil)` es correcto.

## 3.5. Caso `Secuencia(orden :: resto)`

En este caso, la secuencia contiene una primera orden y una lista de órdenes restantes.

La función ejecuta primero:

$$
e'=ejecutar(e,orden)
$$

y posteriormente ejecuta el resto de la secuencia desde el estado `e'`.

Para demostrar la corrección utilizamos inducción estructural sobre la lista de órdenes.

### Caso base

Cuando la lista está vacía, se obtiene `Secuencia(Nil)`, cuyo comportamiento ya fue demostrado como correcto.

### Hipótesis de inducción

Suponemos que la ejecución del `resto` de la secuencia es correcta para cualquier estado inicial.

### Paso inductivo

Consideremos una secuencia:

$$
orden :: resto
$$

Primero se ejecuta `orden` sobre el estado `e`.

Como cada alternativa de una orden básica es correcta, se obtiene el estado correcto `e'`.

Después se ejecuta `resto` desde `e'`.

Por la hipótesis de inducción, la ejecución del resto de la secuencia también es correcta.

Por lo tanto, la secuencia completa se ejecuta correctamente y en el orden establecido.

Además, en cada llamada recursiva se elimina la primera orden de la lista. Como las listas utilizadas son finitas, eventualmente se llega a `Nil`.

## 4. Terminación

La función `girar` siempre termina porque realiza un número finito de comparaciones mediante `match`.

En `ejecutar`, los casos `Avanzar` y `Girar` no realizan llamadas recursivas.

En `Repetir`, el número de repeticiones disminuye en uno en cada llamada recursiva hasta llegar al caso base.

En `Secuencia`, la lista de órdenes pierde un elemento en cada llamada recursiva hasta llegar a `Nil`.

Por lo tanto, para órdenes finitas y un número finito de repeticiones, la función termina.

## 5. Conclusión

Se ha demostrado la corrección de las funciones correspondientes a los puntos 1 y 2 del taller.

La función `girar` es correcta porque contempla todas las combinaciones posibles entre las direcciones y los sentidos de giro.

La función `ejecutar` es correcta porque cada una de