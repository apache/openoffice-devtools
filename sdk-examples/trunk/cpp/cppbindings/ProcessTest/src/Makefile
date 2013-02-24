#**************************************************************
#
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.
#
#**************************************************************

# On *nix can be overridden with
# make PRJ=$OO_SDK_HOME
PRJ=../../../../..
SETTINGS=$(PRJ)/settings

include $(SETTINGS)/settings.mk
include $(SETTINGS)/std.mk
include $(SETTINGS)/dk.mk

# Define non-platform/compiler specific settings
APP_NAME=processtest
MAINTARGET=$(APP_NAME)_Example
TEST_DOC=test.odt

#this should be in odk/settings/std.mk
OUT_DEPLOY=$(OUT)/deploy

OUT_APP_INC=$(OUT_INC)/$(APP_NAME)
OUT_APP_GEN=$(OUT_MISC)/$(APP_NAME)
OUT_APP_OBJ=$(OUT_OBJ)/$(APP_NAME)
OUT_APP_LIB=$(SHAREDLIB_OUT)/$(APP_NAME)
OUT_APP_BIN=$(OUT_BIN)/$(APP_NAME)
OUT_APP_DEPLOY=$(OUT_DEPLOY)/$(APP_NAME)

CXXFILES = main.cxx

OBJFILES = $(patsubst %.cxx,$(OUT_APP_OBJ)/%.$(OBJ_EXT),$(CXXFILES))

TEST_DOC_URL=$(URLPREFIX)$(OUT_APP_BIN)/$(TEST_DOC)


ifneq "$(DEBUG)" ""
	CC_DEFINES+=-DOSL_DEBUG_LEVEL=3
endif

# Targets
.PHONY: ALL
ALL : \
	$(MAINTARGET)

include $(SETTINGS)/stdtarget.mk

$(OUT_APP_OBJ)/%.$(OBJ_EXT) : %.cxx $(SDKTYPEFLAG)
	-$(MKDIR) $(subst /,$(PS),$(@D))
	$(CC) $(CC_FLAGS) $(STL_INCLUDES) $(CC_INCLUDES) -I$(OUT_APP_INC) $(CC_DEFINES) $(CC_OUTPUT_SWITCH)$(subst /,$(PS),$@) $<

$(OUT_APP_BIN)/_$(APP_NAME)$(EXE_EXT) : $(OBJFILES)
	-$(MKDIR) $(subst /,$(PS),$(@D))
	-$(MKDIR) $(subst /,$(PS),$(OUT_APP_GEN))
ifeq "$(OS)" "WIN"
	$(LINK) $(EXE_LINK_FLAGS) /OUT:$@ /MAP:$(OUT_APP_GEN)/$(basename $(@F)).map \
		$(OBJFILES) $(CPPUHELPERLIB) $(CPPULIB) $(SALHELPERLIB) $(SALLIB) $(STLPORTLIB)
else
	$(LINK) $(EXE_LINK_FLAGS) $(LINK_LIBS) -o $@ $(OBJFILES) \
		$(CPPUHELPERLIB) $(CPPULIB) $(SALHELPERLIB) $(SALLIB) $(STLPORTLIB) $(STDC++LIB) $(CPPUHELPERDYLIB) $(CPPUDYLIB) $(SALHELPERDYLIB) $(SALDYLIB)
ifeq "$(OS)" "MACOSX"
	$(INSTALL_NAME_URELIBS_BIN)  $@
endif
endif

$(OUT_APP_BIN)/$(APP_NAME)$(EXE_EXT) : $(OUT_APP_BIN)/_$(APP_NAME)$(EXE_EXT)
	-$(MKDIR) $(subst /,$(PS),$(@D))
	$(COPY) $(subst /,$(PS),$(BIN_DIR)/unoapploader$(EXE_EXT)) $(subst /,$(PS),$@)
# workaround for touch problem under Windows with full qualified paths
	make -t $@

$(OUT_APP_BIN)/$(TEST_DOC) : $(TEST_DOC)
	-$(MKDIR) $(subst /,$(PS),$(@D))
	$(COPY) $(subst /,$(PS),$<) $(subst /,$(PS),$@)


$(MAINTARGET) : $(OUT_APP_BIN)/$(APP_NAME)$(EXE_EXT)
	@echo --------------------------------------------------------------------------------
	@echo Please use the following command to execute the example!
	@echo -
	@echo $(MAKE) $(APP_NAME).run
	@echo --------------------------------------------------------------------------------

LAUNCH_PROG=$(OFFICE_PROGRAM_PATH)$(PS)soffice$(EXE_EXT)
LAUNCH_DOC=$(OUT_APP_BIN)$(PS)$(TEST_DOC)

$(APP_NAME).run: $(OUT_APP_BIN)/$(APP_NAME)$(EXE_EXT) $(OUT_APP_BIN)/$(TEST_DOC)
	cd $(subst /,$(PS),$(OUT_APP_BIN)) && $(basename $@) \
		$(LAUNCH_PROG) $(LAUNCH_DOC)

.PHONY: clean
clean :
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_APP_INC))
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_APP_GEN))
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_APP_OBJ))
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_APP_LIB))
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_APP_BIN))
	-$(DELRECURSIVE) $(subst /,$(PS),$(OUT_APP_DEPLOY))
