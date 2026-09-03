package com.calculatorlife.app.ui.calculator

import com.calculatorlife.app.ui.calculator.gst.GstEngine
import com.calculatorlife.app.ui.calculator.gst.GstMode
import com.calculatorlife.app.ui.calculator.loan.LoanEngine
import com.calculatorlife.app.ui.calculator.percentage.PercentageEngine
import com.calculatorlife.app.ui.calculator.percentage.PercentageMode
import com.calculatorlife.app.ui.calculator.sip.SipEngine
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CalculatorEnginesTest {
    @Test fun percentageValueOf() {
        val result = PercentageEngine.compute(PercentageMode.VALUE_OF, BigDecimal("10"), BigDecimal("200"))
        assertNotNull(result)
        assertEquals(BigDecimal("20.0"), result!!.result)
    }

    @Test fun gstAddAndRemove() {
        val added = GstEngine.compute(GstMode.ADD_GST, BigDecimal("1000"), BigDecimal("18"))
        assertNotNull(added)
        assertEquals(BigDecimal("1180"), added!!.totalAmount)

        val removed = GstEngine.compute(GstMode.REMOVE_GST, BigDecimal("1180"), BigDecimal("18"))
        assertNotNull(removed)
        assertEquals(BigDecimal("1000"), removed!!.baseAmount)
    }

    @Test fun zeroRateLoanIsPrincipalDividedByMonths() {
        val result = LoanEngine.computeEmi(BigDecimal("120000"), BigDecimal.ZERO, 12)
        assertNotNull(result)
        assertEquals(BigDecimal("10000"), result!!.emi)
    }

    @Test fun zeroRateSipReturnsInvestedAmount() {
        val result = SipEngine.compute(BigDecimal("5000"), BigDecimal.ZERO, 12)
        assertNotNull(result)
        assertEquals(BigDecimal("60000"), result!!.maturityValue)
        assertEquals(BigDecimal.ZERO, result.estimatedGains)
    }

    @Test fun invalidInputsAreRejected() {
        assertNull(LoanEngine.computeEmi(BigDecimal.ZERO, BigDecimal("10"), 12))
        assertNull(SipEngine.compute(BigDecimal("5000"), BigDecimal("10"), 0))
    }
}
