package com.calculatorlife.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.calculatorlife.app.R
import com.calculatorlife.app.ui.calculator.age.AgeCalculatorScreen
import com.calculatorlife.app.ui.calculator.average.AverageCalculatorScreen
import com.calculatorlife.app.ui.calculator.breakeven.BreakEvenCalculatorScreen
import com.calculatorlife.app.ui.calculator.commission.CommissionCalculatorScreen
import com.calculatorlife.app.ui.calculator.datediff.DateDifferenceCalculatorScreen
import com.calculatorlife.app.ui.calculator.discount.DiscountCalculatorScreen
import com.calculatorlife.app.ui.calculator.fd.FdCalculatorScreen
import com.calculatorlife.app.ui.calculator.fraction.FractionCalculatorScreen
import com.calculatorlife.app.ui.calculator.gst.GstCalculatorScreen
import com.calculatorlife.app.ui.calculator.incometax.IncomeTaxCalculatorScreen
import com.calculatorlife.app.ui.calculator.inflation.InflationCalculatorScreen
import com.calculatorlife.app.ui.calculator.interest.CompoundInterestCalculatorScreen
import com.calculatorlife.app.ui.calculator.interest.SimpleInterestCalculatorScreen
import com.calculatorlife.app.ui.calculator.investmentreturn.InvestmentReturnCalculatorScreen
import com.calculatorlife.app.ui.calculator.loan.LoanCalculatorScreen
import com.calculatorlife.app.ui.calculator.margin.MarginCalculatorScreen
import com.calculatorlife.app.ui.calculator.markup.MarkupCalculatorScreen
import com.calculatorlife.app.ui.calculator.percentage.PercentageCalculatorScreen
import com.calculatorlife.app.ui.calculator.ppf.PpfCalculatorScreen
import com.calculatorlife.app.ui.calculator.profitloss.ProfitLossCalculatorScreen
import com.calculatorlife.app.ui.calculator.ratio.RatioCalculatorScreen
import com.calculatorlife.app.ui.calculator.rd.RdCalculatorScreen
import com.calculatorlife.app.ui.calculator.salary.SalaryCalculatorScreen
import com.calculatorlife.app.ui.calculator.scientific.ScientificCalculatorScreen
import com.calculatorlife.app.ui.calculator.sip.SipCalculatorScreen
import com.calculatorlife.app.ui.calculator.standard.StandardCalculatorScreen
import com.calculatorlife.app.ui.calculator.tax.TaxCalculatorScreen
import com.calculatorlife.app.ui.calculator.time.TimeCalculatorScreen
import com.calculatorlife.app.ui.common.LocalHistoryNavigator
import com.calculatorlife.app.ui.favorites.FavoritesScreen
import com.calculatorlife.app.ui.favorites.FavoritesViewModel
import com.calculatorlife.app.ui.history.HistoryScreen
import com.calculatorlife.app.ui.menu.MainMenuDrawer
import com.calculatorlife.app.ui.settings.SettingsScreen
import com.calculatorlife.app.ui.vault.VaultScreen
import com.calculatorlife.app.ui.vault.VaultViewModel
import kotlinx.coroutines.launch

/**
 * App root: a single NavHost wrapped by the ☰ drawer, per the spec (no
 * dashboard — [Screen.StandardCalculator] is the start destination).
 * Destinations beyond Standard are added as each phase builds them; the
 * drawer already lists them all but only routes them when implemented.
 */
@Composable
fun CalculatorLifeNavHost(navController: NavHostController = rememberNavController()) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val favoritesViewModel: FavoritesViewModel = viewModel()
    val favoriteRoutes by favoritesViewModel.favoriteRoutes.collectAsState()
    val vaultViewModel: VaultViewModel = viewModel()

    fun navigateTo(route: String) {
        scope.launch { drawerState.close() }
        if (route != currentRoute) {
            navController.navigate(route) { launchSingleTop = true }
        }
    }

    CompositionLocalProvider(LocalHistoryNavigator provides { navigateTo(Screen.History.route) }) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                MainMenuDrawer(
                    currentRoute = currentRoute,
                    favoriteRoutes = favoriteRoutes,
                    onToggleFavorite = favoritesViewModel::toggle,
                    onCalculatorSelected = { screen -> navigateTo(screen.route) },
                    onFavoritesSelected = { navigateTo(Screen.Favorites.route) },
                    onHistorySelected = { navigateTo(Screen.History.route) },
                    onVaultSelected = { navigateTo(Screen.PrivateVault.route) },
                    onSettingsSelected = { navigateTo(Screen.Settings.route) }
                )
            }
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                NavHost(navController = navController, startDestination = Screen.StandardCalculator.route) {
                    composable(Screen.StandardCalculator.route) {
                        StandardCalculatorScreen(
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onVaultPinMatched = { typed -> vaultViewModel.isPinSet() && vaultViewModel.tryUnlockFromCalculator(typed) },
                            onVaultUnlocked = { navigateTo(Screen.PrivateVault.route) }
                        )
                    }
                    composable(Screen.ScientificCalculator.route) {
                        ScientificCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.PercentageCalculator.route) {
                        PercentageCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.FractionCalculator.route) {
                        FractionCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.RatioCalculator.route) {
                        RatioCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.AverageCalculator.route) {
                        AverageCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.AgeCalculator.route) {
                        AgeCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.DateDifferenceCalculator.route) {
                        DateDifferenceCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.TimeCalculator.route) {
                        TimeCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.EmiCalculator.route) {
                        LoanCalculatorScreen(title = stringResource(R.string.calc_emi), onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.LoanCalculator.route) {
                        LoanCalculatorScreen(title = stringResource(R.string.calc_loan), onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.HomeLoanCalculator.route) {
                        LoanCalculatorScreen(title = stringResource(R.string.calc_home_loan), onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.PersonalLoanCalculator.route) {
                        LoanCalculatorScreen(title = stringResource(R.string.calc_personal_loan), onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.CarLoanCalculator.route) {
                        LoanCalculatorScreen(title = stringResource(R.string.calc_car_loan), onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.SipCalculator.route) {
                        SipCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.FdCalculator.route) {
                        FdCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.RdCalculator.route) {
                        RdCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.PpfCalculator.route) {
                        PpfCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.SimpleInterestCalculator.route) {
                        SimpleInterestCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.CompoundInterestCalculator.route) {
                        CompoundInterestCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.GstCalculator.route) {
                        GstCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.DiscountCalculator.route) {
                        DiscountCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.ProfitLossCalculator.route) {
                        ProfitLossCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.SalaryCalculator.route) {
                        SalaryCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.IncomeTaxCalculator.route) {
                        IncomeTaxCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.InflationCalculator.route) {
                        InflationCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.InvestmentReturnCalculator.route) {
                        InvestmentReturnCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.MarginCalculator.route) {
                        MarginCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.MarkupCalculator.route) {
                        MarkupCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.CommissionCalculator.route) {
                        CommissionCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.BreakEvenCalculator.route) {
                        BreakEvenCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.TaxCalculator.route) {
                        TaxCalculatorScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.Favorites.route) {
                        FavoritesScreen(
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onCalculatorSelected = { screen -> navigateTo(screen.route) }
                        )
                    }
                    composable(Screen.History.route) {
                        HistoryScreen(onOpenMenu = { scope.launch { drawerState.open() } })
                    }
                    composable(Screen.PrivateVault.route) {
                        VaultScreen(onOpenMenu = { scope.launch { drawerState.open() } }, viewModel = vaultViewModel)
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onOpenVault = { navigateTo(Screen.PrivateVault.route) }
                        )
                    }
                }
            }
        }
    }
}
