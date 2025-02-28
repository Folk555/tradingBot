package folk.tradingbot.telegram.handlers;

import folk.tradingbot.Utils;
import folk.tradingbot.telegram.TelegramChatListenerService;
import folk.tradingbot.telegram.TelegramClient;
import folk.tradingbot.telegram.configs.TelegramConfigs;
import folk.tradingbot.telegram.models.OrderedChat;
import folk.tradingbot.telegram.models.TelegramUpdateMessage;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drinkless.tdlib.TdApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * callback класс для обработки входящих обновлений
 */
public class TelegramUpdateHandler implements TelegramResultHandler {

    @Autowired
    private TelegramConfigs telegramConfigs;
    private TelegramClient telegramClient = null;
    @Setter
    private TelegramChatListenerService chatListenerService;

    private TdApi.AuthorizationState authorizationState;
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Getter
    private boolean isAuthorizated = false;
    @Getter//все чаты, что есть на текущем клиенте
    private final ConcurrentMap<Long, TdApi.Chat> chats = new ConcurrentHashMap<Long, TdApi.Chat>();
    @Getter//список чатов отсортированных в классическом(как в приложении) порядке
    private final NavigableSet<OrderedChat> chatOrderList = new TreeSet<OrderedChat>();

    private static Logger LOGGER = LogManager.getLogger(TelegramUpdateHandler.class);

    @Override
    public void onResult(TdApi.Object object) {
        switch (object.getConstructor()) {
            case TdApi.UpdateAuthorizationState.CONSTRUCTOR://для авторизации
                onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                break;
            case TdApi.UpdateNewMessage.CONSTRUCTOR: {//новое сообщение
                TdApi.UpdateNewMessage updateChat = (TdApi.UpdateNewMessage) object;
                executor.submit(() -> {
                    chatListenerService.processMessage(new TelegramUpdateMessage(updateChat));
                });
                break;
            }
            case TdApi.UpdateNewChat.CONSTRUCTOR: {//новый чат
                TdApi.UpdateNewChat updateNewChat = (TdApi.UpdateNewChat) object;
                TdApi.Chat chat = updateNewChat.chat;
                synchronized (chat) {
                    chats.put(chat.id, chat);
                    TdApi.ChatPosition[] positions = chat.positions;
                    chat.positions = new TdApi.ChatPosition[0];
                    setChatPositions(chat, positions);
                }
                break;
            }
            //по факту сейчас не юзается
            case TdApi.UpdateUser.CONSTRUCTOR://инфа о юзере(любом)
                TdApi.UpdateUser updateUser = (TdApi.UpdateUser) object;
                //users.put(updateUser.user.id, updateUser.user);
                break;
            case TdApi.UpdateChatPosition.CONSTRUCTOR: {
                TdApi.UpdateChatPosition updateChat = (TdApi.UpdateChatPosition) object;
                if (updateChat.position.list.getConstructor() != TdApi.ChatListMain.CONSTRUCTOR) {
                    break;
                }

                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    int i;
                    for (i = 0; i < chat.positions.length; i++) {
                        if (chat.positions[i].list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
                            break;
                        }
                    }
                    TdApi.ChatPosition[] new_positions = new TdApi.ChatPosition[chat.positions.length + (updateChat.position.order == 0 ? 0 : 1) - (i < chat.positions.length ? 1 : 0)];
                    int pos = 0;
                    if (updateChat.position.order != 0) {
                        new_positions[pos++] = updateChat.position;
                    }
                    for (int j = 0; j < chat.positions.length; j++) {
                        if (j != i) {
                            new_positions[pos++] = chat.positions[j];
                        }
                    }
                    assert pos == new_positions.length;

                    setChatPositions(chat, new_positions);
                }
                break;
            }
            case TdApi.UpdateChatLastMessage.CONSTRUCTOR: {
                TdApi.UpdateChatLastMessage updateChat = (TdApi.UpdateChatLastMessage) object;
                //было обновлено последнее сообщение в чате по id = updateChat.chatId
                break;
            }
            case TdApi.UpdateSupergroup.CONSTRUCTOR: {
                //данные о группе или канале были изменены
                break;
            }
            case TdApi.UpdateChatAddedToList.CONSTRUCTOR: {
                //появился новый чат
                break;
            }
            case TdApi.UpdateBasicGroup.CONSTRUCTOR: {
                //данные о базовой группе были изменены
                break;
            }
            case TdApi.UpdateSupergroupFullInfo.CONSTRUCTOR: {
                ////данные о группе или канале в "какой-то структуре" были изменены
                break;
            }
            case TdApi.UpdateUnreadMessageCount.CONSTRUCTOR, TdApi.UpdateChatFolders.CONSTRUCTOR,
                 TdApi.UpdateReactionNotificationSettings.CONSTRUCTOR, TdApi.UpdateScopeNotificationSettings.CONSTRUCTOR,
                 TdApi.UpdateChatThemes.CONSTRUCTOR, TdApi.UpdateAvailableMessageEffects.CONSTRUCTOR,
                 TdApi.UpdateFileDownloads.CONSTRUCTOR, TdApi.UpdateDiceEmojis.CONSTRUCTOR,
                 TdApi.UpdateDefaultBackground.CONSTRUCTOR, TdApi.UpdateAttachmentMenuBots.CONSTRUCTOR,
                 TdApi.UpdateSpeechRecognitionTrial.CONSTRUCTOR, TdApi.UpdateProfileAccentColors.CONSTRUCTOR,
                 TdApi.UpdateAccentColors.CONSTRUCTOR, TdApi.UpdateAnimationSearchParameters.CONSTRUCTOR,
                 TdApi.UpdateOption.CONSTRUCTOR, TdApi.UpdateSavedMessagesTopic.CONSTRUCTOR,
                 TdApi.UpdateHavePendingNotifications.CONSTRUCTOR, TdApi.UpdateConnectionState.CONSTRUCTOR,
                 TdApi.UpdateStoryStealthMode.CONSTRUCTOR, TdApi.UpdateUnreadChatCount.CONSTRUCTOR,
                 TdApi.UpdateActiveEmojiReactions.CONSTRUCTOR, TdApi.UpdateDefaultReactionType.CONSTRUCTOR,
                 TdApi.UpdateChatReadInbox.CONSTRUCTOR, TdApi.UpdateMessageInteractionInfo.CONSTRUCTOR,
                 TdApi.UpdateDeleteMessages.CONSTRUCTOR, TdApi.UpdateSuggestedActions.CONSTRUCTOR,
                 TdApi.UpdateChatAvailableReactions.CONSTRUCTOR, TdApi.UpdateChatNotificationSettings.CONSTRUCTOR,
                 TdApi.UpdateMessageEdited.CONSTRUCTOR, TdApi.UpdateMessageContent.CONSTRUCTOR,
                 TdApi.UpdateChatActiveStories.CONSTRUCTOR, TdApi.UpdateChatReadOutbox.CONSTRUCTOR,
                 TdApi.UpdateUserStatus.CONSTRUCTOR, TdApi.UpdateChatAction.CONSTRUCTOR,
                 TdApi.UpdateChatReplyMarkup.CONSTRUCTOR, TdApi.UpdateMessageSendSucceeded.CONSTRUCTOR: {
                //прочие обновления с которыми я не работал
                break;
            }
            default: {
                LOGGER.warn("Пришел телеграмм объект для которого нет обработчика\n {}", object);
            }
        }
    }

    private void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        if (authorizationState != null) {
            this.authorizationState = authorizationState;
        }
        switch (authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                TdApi.SetTdlibParameters request = new TdApi.SetTdlibParameters();
                request.databaseDirectory = telegramConfigs.tdlib;
                request.useMessageDatabase = true;
                request.useSecretChats = true;
                request.apiId = telegramConfigs.apiId;
                request.apiHash = telegramConfigs.apiHash;
                request.systemLanguageCode = "en";
                request.deviceModel = "Desktop";
                request.applicationVersion = "1.0";

                telegramClient.send(request, new TelegramAuthorizationHandler());
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                String phoneNumber = Utils.printPromptAndGetAns("Please enter phone number: ");
                telegramClient.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null), new TelegramAuthorizationHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
                String code = Utils.printPromptAndGetAns("Please enter authentication code: ");
                telegramClient.send(new TdApi.CheckAuthenticationCode(code), new TelegramAuthorizationHandler());
                break;
            }
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                isAuthorizated = true;
                break;
            default:
                LOGGER.error("Unsupported authorization state: {}", authorizationState);
        }
    }

    private void setChatPositions(TdApi.Chat chat, TdApi.ChatPosition[] positions) {
        synchronized (chatOrderList) {
            synchronized (chat) {
                for (TdApi.ChatPosition position : chat.positions) {
                    if (position.list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
                        boolean isRemoved = chatOrderList.remove(new OrderedChat(chat.id, position));
                        assert isRemoved;
                    }
                }

                chat.positions = positions;

                for (TdApi.ChatPosition position : chat.positions) {
                    if (position.list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
                        boolean isAdded = chatOrderList.add(new OrderedChat(chat.id, position));
                        assert isAdded;
                    }
                }
            }
        }
    }

    public void setTelegramClient(TelegramClient telegramClient) {
        if (this.telegramClient != null) {
            LOGGER.error("Возникло исключение в методе setTelegramClient");
            throw new RuntimeException("В 1 TelegramUpdateHandler телеграм клиент можно задать только 1 раз");
        }
        this.telegramClient = telegramClient;
    }
}
