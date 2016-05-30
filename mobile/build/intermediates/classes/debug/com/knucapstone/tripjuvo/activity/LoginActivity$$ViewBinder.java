// Generated code from Butter Knife. Do not modify!
package com.knucapstone.tripjuvo.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class LoginActivity$$ViewBinder<T extends com.knucapstone.tripjuvo.activity.LoginActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689614, "field '_emailText'");
    target._emailText = finder.castView(view, 2131689614, "field '_emailText'");
    view = finder.findRequiredView(source, 2131689615, "field '_passwordText'");
    target._passwordText = finder.castView(view, 2131689615, "field '_passwordText'");
    view = finder.findRequiredView(source, 2131689616, "field '_loginButton'");
    target._loginButton = finder.castView(view, 2131689616, "field '_loginButton'");
    view = finder.findRequiredView(source, 2131689617, "field '_signupLink'");
    target._signupLink = finder.castView(view, 2131689617, "field '_signupLink'");
  }

  @Override public void unbind(T target) {
    target._emailText = null;
    target._passwordText = null;
    target._loginButton = null;
    target._signupLink = null;
  }
}
