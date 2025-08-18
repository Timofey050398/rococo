package timofeyqa.rococo.jupiter.extension;

import org.apache.commons.lang3.StringUtils;
import timofeyqa.rococo.config.Config;
import timofeyqa.rococo.jupiter.annotation.User;
import timofeyqa.rococo.model.rest.UserJson;
import timofeyqa.rococo.service.UserClient;
import timofeyqa.rococo.service.db.UsersDbClient;
import timofeyqa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class UserExtension implements
        BeforeEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private static final Config CFG = Config.getInstance();

    private final UserClient usersClient = new UsersDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())) {
                        final String username = RandomDataUtils.randomUsername();

                        UserJson user = usersClient.createUser(
                                username,
                                CFG.defaultPassword()
                        );
                        var userBuilder = user.toBuilder();
                        boolean needToUpdate = false;

                        if (!StringUtils.isBlank(userAnno.firstname())) {
                            userBuilder.firstname(userAnno.firstname());
                            needToUpdate = true;
                        }
                        if (!StringUtils.isBlank(userAnno.lastname())) {
                            userBuilder.lastname(userAnno.lastname());
                            needToUpdate = true;
                        }
                        if (!StringUtils.isBlank(userAnno.avatar())) {
                            userBuilder.avatar(userAnno.avatar());
                            needToUpdate = true;
                        }

                        if(needToUpdate){
                            user = usersClient.updateUser(userBuilder.build());
                        }

                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                user
                        );
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJson.class);
    }

    public static UserJson createdUser() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
    }

    public static void setUser(UserJson user) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(context.getUniqueId(), user);
    }
}