/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.modules.RailcraftModuleManager.Stage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

public abstract class RailcraftModulePayload implements IRailcraftModule {

    private static final ModuleEventHandler BLANK_EVENT_HANDLER = new ModuleEventHandler() {
    };
    private final LinkedHashSet<IRailcraftObjectContainer<?>> objectContainers = new LinkedHashSet<>();
    private final ModuleEventHandler baseEventHandler = new BaseModuleEventHandler(this);
    private ModuleEventHandler enabledEventHandler = BLANK_EVENT_HANDLER;
    private ModuleEventHandler disabledEventHandler = BLANK_EVENT_HANDLER;

    public final void setEnabledEventHandler(ModuleEventHandler enabledEventHandler) {
        this.enabledEventHandler = enabledEventHandler;
    }

    public final void setDisabledEventHandler(ModuleEventHandler disabledEventHandler) {
        this.disabledEventHandler = disabledEventHandler;
    }

    public final void add(IRailcraftObjectContainer<?>... objects) {
        if (RailcraftModuleManager.getStage() != Stage.LOADING)
            throw new RuntimeException("You can only associate Railcraft Objects with a Module during the Loading phase!");
        Arrays.stream(objects).map(IRailcraftObjectContainer::getContainer).forEach(objectContainers::add);
    }

    public final boolean isDefiningObject(IRailcraftObjectContainer<?> object) {
        return objectContainers.contains(object);
    }

    public final Collection<IRailcraftObjectContainer<?>> getObjects() {
        return Collections.unmodifiableSet(objectContainers);
    }

    @Override
    public final ModuleEventHandler getModuleEventHandler(boolean enabled) {
        if (enabled)
            return baseEventHandler;
        return disabledEventHandler;
    }

    @Override
    public String toString() {
        return "RailcraftModule{" + getClass().getAnnotation(RailcraftModule.class).value() + "}";
    }

    private final class BaseModuleEventHandler implements ModuleEventHandler {
        private final IRailcraftModule owner;

        private BaseModuleEventHandler(IRailcraftModule owner) {
            this.owner = owner;
        }

        @Override
        public void construction() {
            enabledEventHandler.construction();
        }

        @Override
        public void preInit() {
            objectContainers.forEach(c -> c.addedBy(owner.getClass()));
            //Must mark all items as added first because recipe registry may register items in random order
            objectContainers.forEach(IRailcraftObjectContainer::register);
            enabledEventHandler.preInit();
        }

        @Override
        public void init() {
            objectContainers.forEach(roc -> {
                //Use a set to avoid redefine recipes
                if (!RailcraftModuleManager.definedContainers.contains(roc)) {
                    roc.defineRecipes();
                    RailcraftModuleManager.definedContainers.add(roc);
                }
            });
            enabledEventHandler.init();
        }

        @Override
        public void postInit() {
            // Now we don't need to call them individually!
            objectContainers.forEach(IRailcraftObjectContainer::finalizeDefinition);
            enabledEventHandler.postInit();
        }
    }

}
