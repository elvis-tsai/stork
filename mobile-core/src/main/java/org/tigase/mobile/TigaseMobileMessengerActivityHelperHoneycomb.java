package org.tigase.mobile;

import java.util.List;

import org.tigase.mobile.MultiJaxmpp.ChatWrapper;
import org.tigase.mobile.roster.CPresence;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Room.State;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(11)
public class TigaseMobileMessengerActivityHelperHoneycomb extends TigaseMobileMessengerActivityHelper {

	protected TigaseMobileMessengerActivityHelperHoneycomb(TigaseMobileMessengerActivity activity) {
		super(activity);
	}

	@Override
	public void invalidateOptionsMenu() {
		activity.invalidateOptionsMenu();
	}

	@Override
	public void setShowAsAction(MenuItem item, int actionEnum) {
		item.setShowAsAction(actionEnum);
	}

	@Override
	public void updateActionBar() {
		activity.viewPager.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int currentPage = activity.getCurrentPage();

				ActionBar actionBar = activity.getActionBar();
				if (currentPage != 1 && !isXLarge()) {
					activity.drawerLayout.setDrawerListener(null);
					activity.drawerToggle.setDrawerIndicatorEnabled(false);
				}
				else {
					activity.drawerLayout.setDrawerListener(activity.drawerToggle);
					activity.drawerToggle.setDrawerIndicatorEnabled(true);
				}
				
				actionBar.setDisplayHomeAsUpEnabled(true);
				//actionBar.setHomeButtonEnabled(true);
//				actionBar.setDisplayHomeAsUpEnabled(currentPage != 1 && !isXLarge());

				// Setting subtitle to show who we chat with
				ChatWrapper c = activity.getChatByPageIndex(currentPage);
				if (c != null) {
					// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE,
					// ActionBar.DISPLAY_SHOW_TITLE);
					actionBar.setSubtitle(null);
					actionBar.setDisplayShowCustomEnabled(true);
					View view = actionBar.getCustomView();
					if (view == null) {
						actionBar.setCustomView(R.layout.actionbar_status);
						view = actionBar.getCustomView();
					}
					String subtitle = null;
					int icon = 0;
					if (c.getChat() != null) {
						BareJID jid = c.getChat().getJid().getBareJid();
						RosterItem ri = c.getChat().getSessionObject().getRoster().get(jid);
						subtitle = "Chat with " + (ri != null ? ri.getName() : jid.toString());

						icon = R.drawable.user_offline;
						CPresence p = new RosterDisplayTools(activity).getShowOf(c.getChat().getSessionObject(),
								c.getChat().getJid().getBareJid());
						c.getChat().getSessionObject().getPresence().getPresence(c.getChat().getJid());
						switch (p) {
						case chat:
							icon = R.drawable.user_free_for_chat;
							break;
						case online:
							icon = R.drawable.user_available;
							break;
						case away:
							icon = R.drawable.user_away;
							break;
						case xa:
							icon = R.drawable.user_extended_away;
							break;
						case dnd:
							icon = R.drawable.user_busy;
							break;
						default:
							break;
						}
					} else if (c.getRoom() != null) {
						subtitle = "Room " + c.getRoom().getRoomJid().toString();
						icon = R.drawable.user_offline;
						if (c.getRoom().getState() == State.joined) {
							icon = R.drawable.user_available;
						}
					}
					// actionBar.setSubtitle(subtitle);
					if (view != null) {
						Holder holder = (Holder) view.getTag();
						if (holder == null) {
							holder = new Holder();
							holder.title = (TextView) view.findViewById(R.id.title);
							holder.description = (TextView) view.findViewById(R.id.description);
							holder.status = (ImageView) view.findViewById(R.id.status);
							view.setTag(holder);
						}
						holder.title.setText(actionBar.getTitle());
						holder.description.setText(subtitle);
						holder.status.setImageResource(icon);
					}
				} else {
					actionBar.setDisplayShowCustomEnabled(false);
					if (currentPage == 0) {
						actionBar.setSubtitle("Accounts");
					} else {
						actionBar.setSubtitle(null);
					}
				}
			}

		});
	}

	@Override
	public void updateActionBar(int itemHashCode) {
		List<ChatWrapper> chats = activity.getChatList();
		for (int i = 0; i < chats.size(); i++) {
			ChatWrapper chat = chats.get(i);
			if (chat.hashCode() == itemHashCode) {
				updateActionBar();
				return;
			}
		}
	}

	private class Holder {
		TextView title;
		TextView description;
		ImageView status;
	}
}
