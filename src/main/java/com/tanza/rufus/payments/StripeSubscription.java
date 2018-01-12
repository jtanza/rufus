import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import com.tanza.rufus.core.User;;


public class StripeSubscription {

    private enum SubscriptionPlan {
        INDIE,
        SMALLPRESS, 
        CONGLOMERATE
    }
    public StripeSubscription(String apiKey, String email, SubscriptionPlan plan, User user){}
}
