package no.sigurof.gravity.utils.operators

import io.kotlintest.TestCase
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import org.joml.Vector3f

internal class OperatorsKtTest(
    private var x0: Float = 1f,
    private var x1: Float = 2f,
    private var x2: Float = 3f,
    private var y0: Float = 10f,
    private var y1: Float = 100f,
    private var y2: Float = 4f,
    private var a: Vector3f = Vector3f(x0, x1, x2),
    private var b: Vector3f = Vector3f(y0, y1, y2)

) : StringSpec({
    "subtraction works element by element and the original operands remain unaltered" {
        (a - b).shouldBe(Vector3f(x0 - y0, x1 - y1, x2 - y2))
        a.shouldBe(Vector3f(x0, x1, x2))
        b.shouldBe(Vector3f(y0, y1, y2))
    }

    "addition works element by element and the original operands remain unaltered" {
        (a + b).shouldBe(Vector3f(x0 + y0, x1 + y1, x2 + y2))
        a.shouldBe(Vector3f(x0, x1, x2))
        b.shouldBe(Vector3f(y0, y1, y2))
    }

    "unary minus returns a new, negated vector. The original one remains unaltered" {
        (-a).shouldBe(Vector3f(-x0, -x1, -x2))
        a.shouldBe(Vector3f(x0, x1, x2))
    }


    "times * should correspond to the dot product (the sum of the element-wise product of two vectors)" {
        (a * b).shouldBe(x0 * y0 + x1 * y1 + x2 * y2)
        a.shouldBe(Vector3f(x0, x1, x2))
        b.shouldBe(Vector3f(y0, y1, y2))
    }

    "times * " should {

        val originalScale = 12.3f
        val scale = originalScale
        val descr = "return a copy of the vector scaled by the float's value"

        "for Float*Vector3f $descr"{
            (scale * a).shouldBe(Vector3f(x0 * scale, x1 * scale, x2 * scale))
            scale.shouldBe(originalScale)
            a.shouldBe(Vector3f(x0, x1, x2))
        }

        "for Vector3f * Float $descr"{
            (a * scale).shouldBe(Vector3f(x0 * scale, x1 * scale, x2 * scale))
            scale.shouldBe(originalScale)
            a.shouldBe(Vector3f(x0, x1, x2))
        }
    }

    "+= should work elementwise and the lefthand operand is changed, righthand unaltered"{
        var q = Vector3f(a)
        q += b
        q.shouldBe(Vector3f(x0 + y0, x1 + y1, x2 + y2))
        b.shouldBe(Vector3f(y0, y1, y2))
    }

    "-= should work elementwise and the lefthand operand is changed, righthand unaltered"{
        var q = Vector3f(a)
        q -= b
        q.shouldBe(Vector3f(x0 - y0, x1 - y1, x2 - y2))
        b.shouldBe(Vector3f(y0, y1, y2))
    }

    "normalized() should return the result of the normalize() function without altering `this`"{
        val q = Vector3f(a)
        val qNorm = q.normalized()
        qNorm shouldBe(a.normalize(Vector3f()))
        qNorm shouldNotBe(a)
        q shouldBe(a)

    }
}) {

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        x0 = 1f
        x1 = 2f
        x2 = 3f
        y0 = 10f
        y1 = 100f
        y2 = 4f
        a = Vector3f(x0, x1, x2)
        b = Vector3f(y0, y1, y2)
    }

}


