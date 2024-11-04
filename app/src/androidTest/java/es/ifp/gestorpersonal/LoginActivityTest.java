package es.ifp.gestorpersonal;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.Matchers.is;

import android.view.WindowManager;

import androidx.test.espresso.Root;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void loginActivityTest() {
        // Prueba de login normal
        performLogin("123", "123", true);

        // Prueba con usuario incorrecto
        performLogin("wrongUser", "password123", false);

        // Prueba con contraseña incorrecta
        performLogin("123", "wrongPassword", false);

        // Prueba con usuario vacío
        performLogin("", "password123", false);

        // Prueba con contraseña vacía
        performLogin("123", "", false);

        // Prueba con ambos campos vacíos
        performLogin("", "", false);
    }

    private void performLogin(String username, String password, boolean shouldSucceed) {
        // Ingresar nombre de usuario
        onView(withId(R.id.caja1_main))
                .perform(replaceText(username), closeSoftKeyboard());

        // Ingresar contraseña
        onView(withId(R.id.caja2_main))
                .perform(replaceText(password), closeSoftKeyboard());

        // Intentar iniciar sesión
        onView(withId(R.id.boton1_main)).perform(click());

        // Pausa para esperar que la UI se actualice
        try {
            Thread.sleep(3000); // Espera 3 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (shouldSucceed) {
            // Verificar que el botón esté visible en ModulosActivity
            onView(withId(R.id.botongim_modulos)).check(matches(isDisplayed()));

            // Abrir el menú de opciones
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());


            // Clic en la opción de "Cerrar sesión"
            onView(withText("Cerrar sesión")).perform(click());

            // Esperar a que vuelva a cargar la actividad de inicio de sesión
            try {
                Thread.sleep(3000); // Espera 3 segundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Verificar que la pantalla de login esté visible
            onView(withId(R.id.caja1_main)).check(matches(isDisplayed()));
        }
    }

}


