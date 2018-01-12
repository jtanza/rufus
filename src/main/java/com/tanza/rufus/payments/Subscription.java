import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import com.tanza.rufus.core.User;;

public enum SubscriptionPlans {
    INDIE,
    SMALLPRESS, 
    CONGLOMERATE
}

public class StripeSubscription {
    public StripeSubscription(String apiKey, String email, SubscriptionPlans plan, User user){}
}
