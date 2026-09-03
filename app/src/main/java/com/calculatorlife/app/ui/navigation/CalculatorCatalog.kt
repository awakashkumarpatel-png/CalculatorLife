package com.calculatorlife.app.ui.navigation

import androidx.annotation.StringRes
import com.calculatorlife.app.R

enum class CalculatorCategory { BASIC, FINANCE, BUSINESS }

/**
 * One row in the menu. [implemented] is flipped to true as each calculator
 * is actually built in later phases — until then the menu shows it, greyed
 * out, rather than pretending it works.
 */
data class CalculatorMenuItem(
    val screen: Screen,
    @StringRes val titleRes: Int,
    val category: CalculatorCategory,
    val implemented: Boolean
)

object CalculatorCatalog {
    val items: List<CalculatorMenuItem> = listOf(
        // Basic
        CalculatorMenuItem(Screen.StandardCalculator, R.string.calc_standard, CalculatorCategory.BASIC, implemented = true),
        CalculatorMenuItem(Screen.ScientificCalculator, R.string.calc_scientific, CalculatorCategory.BASIC, implemented = true),
        CalculatorMenuItem(Screen.PercentageCalculator, R.string.calc_percentage, CalculatorCategory.BASIC, implemented = true),
        CalculatorMenuItem(Screen.FractionCalculator, R.string.calc_fraction, CalculatorCategory.BASIC, implemented = true),
        CalculatorMenuItem(Screen.RatioCalculator, R.string.calc_ratio, CalculatorCategory.BASIC, implemented = true),
        CalculatorMenuItem(Screen.AverageCalculator, R.string.calc_average, CalculatorCategory.BASIC, implemented = true),
        CalculatorMenuItem(Screen.AgeCalculator, R.string.calc_age, CalculatorCategory.BASIC, implemented = true),
        CalculatorMenuItem(Screen.DateDifferenceCalculator, R.string.calc_date_difference, CalculatorCategory.BASIC, implemented = true),
        CalculatorMenuItem(Screen.TimeCalculator, R.string.calc_time, CalculatorCategory.BASIC, implemented = true),

        // Finance & Investment
        CalculatorMenuItem(Screen.EmiCalculator, R.string.calc_emi, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.SipCalculator, R.string.calc_sip, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.LoanCalculator, R.string.calc_loan, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.HomeLoanCalculator, R.string.calc_home_loan, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.PersonalLoanCalculator, R.string.calc_personal_loan, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.CarLoanCalculator, R.string.calc_car_loan, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.FdCalculator, R.string.calc_fd, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.RdCalculator, R.string.calc_rd, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.PpfCalculator, R.string.calc_ppf, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.SimpleInterestCalculator, R.string.calc_simple_interest, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.CompoundInterestCalculator, R.string.calc_compound_interest, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.GstCalculator, R.string.calc_gst, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.DiscountCalculator, R.string.calc_discount, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.ProfitLossCalculator, R.string.calc_profit_loss, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.SalaryCalculator, R.string.calc_salary, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.IncomeTaxCalculator, R.string.calc_income_tax, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.InflationCalculator, R.string.calc_inflation, CalculatorCategory.FINANCE, implemented = true),
        CalculatorMenuItem(Screen.InvestmentReturnCalculator, R.string.calc_investment_return, CalculatorCategory.FINANCE, implemented = true),

        // Business
        CalculatorMenuItem(Screen.MarginCalculator, R.string.calc_margin, CalculatorCategory.BUSINESS, implemented = true),
        CalculatorMenuItem(Screen.MarkupCalculator, R.string.calc_markup, CalculatorCategory.BUSINESS, implemented = true),
        CalculatorMenuItem(Screen.CommissionCalculator, R.string.calc_commission, CalculatorCategory.BUSINESS, implemented = true),
        CalculatorMenuItem(Screen.BreakEvenCalculator, R.string.calc_break_even, CalculatorCategory.BUSINESS, implemented = true),
        CalculatorMenuItem(Screen.TaxCalculator, R.string.calc_tax, CalculatorCategory.BUSINESS, implemented = true)
    )
}
