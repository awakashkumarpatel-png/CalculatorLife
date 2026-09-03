package com.calculatorlife.app.ui.navigation

/**
 * Every navigable destination in the app. Only [StandardCalculator] is wired
 * up in Phase 1 — the rest are declared now so routes are stable as later
 * phases add their screens, but they are not reachable yet (the menu grays
 * them out rather than opening a fake screen).
 */
sealed class Screen(val route: String) {
    // Basic
    data object StandardCalculator : Screen("calc/standard")
    data object ScientificCalculator : Screen("calc/scientific")
    data object PercentageCalculator : Screen("calc/percentage")
    data object FractionCalculator : Screen("calc/fraction")
    data object RatioCalculator : Screen("calc/ratio")
    data object AverageCalculator : Screen("calc/average")
    data object AgeCalculator : Screen("calc/age")
    data object DateDifferenceCalculator : Screen("calc/date_difference")
    data object TimeCalculator : Screen("calc/time")

    // Finance & Investment
    data object EmiCalculator : Screen("calc/emi")
    data object SipCalculator : Screen("calc/sip")
    data object LoanCalculator : Screen("calc/loan")
    data object HomeLoanCalculator : Screen("calc/home_loan")
    data object PersonalLoanCalculator : Screen("calc/personal_loan")
    data object CarLoanCalculator : Screen("calc/car_loan")
    data object FdCalculator : Screen("calc/fd")
    data object RdCalculator : Screen("calc/rd")
    data object PpfCalculator : Screen("calc/ppf")
    data object SimpleInterestCalculator : Screen("calc/simple_interest")
    data object CompoundInterestCalculator : Screen("calc/compound_interest")
    data object GstCalculator : Screen("calc/gst")
    data object DiscountCalculator : Screen("calc/discount")
    data object ProfitLossCalculator : Screen("calc/profit_loss")
    data object SalaryCalculator : Screen("calc/salary")
    data object IncomeTaxCalculator : Screen("calc/income_tax")
    data object InflationCalculator : Screen("calc/inflation")
    data object InvestmentReturnCalculator : Screen("calc/investment_return")

    // Business
    data object MarginCalculator : Screen("calc/margin")
    data object MarkupCalculator : Screen("calc/markup")
    data object CommissionCalculator : Screen("calc/commission")
    data object BreakEvenCalculator : Screen("calc/break_even")
    data object TaxCalculator : Screen("calc/tax")

    // App sections
    data object Favorites : Screen("favorites")
    data object History : Screen("history")
    data object PrivateVault : Screen("vault")
    data object Settings : Screen("settings")
}
