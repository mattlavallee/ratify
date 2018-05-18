package io.github.mattlavallee.rightify.core

import io.github.mattlavallee.ratify.core.FormError
import org.junit.Assert
import org.junit.Test

class FormErrorUnitTest {
    @Test
    fun formError_initSuccess() {
        Assert.assertNotEquals(FormError(), null)
    }

    @Test
    fun formError_hasErrors() {
        var testObj = FormError()
        Assert.assertEquals(testObj.hasErrors(), false)

        testObj.missing.add("foo")
        Assert.assertEquals(testObj.hasErrors(), true)
    }

    @Test
    fun formError_errorMessage() {
        var testObj = FormError()
        Assert.assertEquals(testObj.generateErrorMessage(), "Please provide values for: ")

        testObj.missing.add("foo")
        testObj.missing.add("bar")
        Assert.assertEquals(testObj.generateErrorMessage(), "Please provide values for: foo, bar")
    }
}
