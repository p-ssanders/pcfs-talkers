package com.pivotal.pcfs.slack.talkers;

import java.util.Collection;
import java.util.Collections;

public class SlackService {

  public Collection<SlackMessage> getChannelMessageHistory(String channelName) {
    // G04NWJQ90
    return Collections.singletonList(new SlackMessage("sam", "hey there"));
  }

  /* history response shape
  {
    "ok": true,
    "messages": [],
    "has_more": true
  }
   */

  /* a messagex
{
    //"client_msg_id": "eb4ecd7d-25cd-484c-9cb1-e41b8c152114",
    "type": "message",
    //"text": "(feel free to DM me)",
    "user": "U03DZ8T2R",
    //"ts": "1557962476.069900",
    //"thread_ts": "1557950340.004100",
    //"parent_user_id": "U2PH6Q7D5"
}
 */
}
