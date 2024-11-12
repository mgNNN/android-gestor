package es.ifp.gestorpersonal;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StartActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void startActivityTest() {
        // Prueba de registro normal
        performRegistration("nuevo1", "nuevo",  "prueba@example.com", "666777888");

        // Prueba con campos vacíos
        performRegistration("", "", "", "");

        // Prueba con nombre de usuario duplicado
        performRegistration("existingUser", "password123",  "existing@example.com","666777888");

        // Prueba con contraseñas no coincidentes
        performRegistration("nuevoUser", "password123", "test@example.com","666777888");

        // Prueba con contraseña demasiado corta
        performRegistration("shortPwdUser", "123", "short@example.com", "666777888");

        // Prueba con nombre de usuario con caracteres especiales
        performRegistration("user@name!", "password123",  "special@example.com","666777888");

        // Prueba con nombre de usuario largo
        performRegistration("aVeryLongUsernameThatExceedsLimit", "password123", "long@example.com","666777888");
    }

    // Método auxiliar para realizar el registro con diferentes valores de entrada
    private void performRegistration(String username, String password, String email, String numeroTelefono) {

        // Ir a la pantalla de registro
        onView(allOf(withId(R.id.boton2_main), withText("Registrarse"),
                childAtPosition(allOf(withId(R.id.main),
                                childAtPosition(withId(android.R.id.content), 0)),
                        3), isDisplayed())).perform(click());

        // Ingresar nombre de usuario
        onView(allOf(withId(R.id.caja1_register),
                childAtPosition(allOf(withId(R.id.main),
                                childAtPosition(withId(android.R.id.content), 0)),
                        1), isDisplayed())).perform(replaceText(username), closeSoftKeyboard());

        // Ingresar contraseña
        onView(allOf(withId(R.id.caja2_register),
                childAtPosition(allOf(withId(R.id.main),
                                childAtPosition(withId(android.R.id.content), 0)),
                        3), isDisplayed())).perform(replaceText(password), closeSoftKeyboard());

        // Ingresar correo electrónico
        onView(allOf(withId(R.id.caja3_register),
                childAtPosition(allOf(withId(R.id.main),
                                childAtPosition(withId(android.R.id.content), 0)),
                        5), isDisplayed())).perform(replaceText(email), closeSoftKeyboard());

        // Ingresar numero de telefono
        onView(allOf(withId(R.id.caja4_register),
                childAtPosition(allOf(withId(R.id.main),
                                childAtPosition(withId(android.R.id.content), 0)),
                        9), isDisplayed())).perform(replaceText(numeroTelefono), closeSoftKeyboard());

        // Intentar registrarse
        onView(allOf(withId(R.id.boton2_register), withText("Registrarse"),
                childAtPosition(allOf(withId(R.id.main),
                                childAtPosition(withId(android.R.id.content), 0)),
                        7), isDisplayed())).perform(click());

        // Pausa de 15 segundos
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Volver a la pantalla de inicio
        onView(allOf(withId(R.id.boton1_register), withText("Volver"),
                childAtPosition(allOf(withId(R.id.main),
                                childAtPosition(withId(android.R.id.content), 0)),
                        6), isDisplayed())).perform(click());
    }

    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
